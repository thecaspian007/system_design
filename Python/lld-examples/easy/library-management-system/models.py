"""
SQLAlchemy Models for Library Management System

This module contains the database models for the library management system.
"""

from sqlalchemy import Column, Integer, String, Float, Boolean, Date, DateTime, ForeignKey, Text, Enum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, validates
from sqlalchemy.sql import func
from datetime import datetime, date, timedelta
from enum import Enum as PyEnum
import uuid

Base = declarative_base()


class BookStatus(PyEnum):
    """Enum for book status"""
    AVAILABLE = "AVAILABLE"
    CHECKED_OUT = "CHECKED_OUT"
    RESERVED = "RESERVED"
    DAMAGED = "DAMAGED"
    LOST = "LOST"


class MembershipType(PyEnum):
    """Enum for membership types"""
    STUDENT = "STUDENT"
    FACULTY = "FACULTY"
    STAFF = "STAFF"
    GENERAL = "GENERAL"


class MemberStatus(PyEnum):
    """Enum for member status"""
    ACTIVE = "ACTIVE"
    SUSPENDED = "SUSPENDED"
    EXPIRED = "EXPIRED"
    BLOCKED = "BLOCKED"


class TransactionStatus(PyEnum):
    """Enum for transaction status"""
    ACTIVE = "ACTIVE"
    RETURNED = "RETURNED"
    OVERDUE = "OVERDUE"
    LOST = "LOST"
    DAMAGED = "DAMAGED"


class Book(Base):
    """Book model representing books in the library"""
    
    __tablename__ = "books"
    
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False, index=True)
    author = Column(String(100), nullable=False, index=True)
    isbn = Column(String(20), unique=True, nullable=False, index=True)
    genre = Column(String(50), nullable=False, index=True)
    publication_date = Column(Date)
    total_copies = Column(Integer, nullable=False, default=1)
    available_copies = Column(Integer, nullable=False, default=1)
    price = Column(Float, nullable=False, default=0.0)
    description = Column(Text)
    status = Column(Enum(BookStatus), nullable=False, default=BookStatus.AVAILABLE)
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())
    
    # Relationships
    transactions = relationship("BookTransaction", back_populates="book")
    
    def is_available(self) -> bool:
        """Check if book is available for borrowing"""
        return self.available_copies > 0 and self.status == BookStatus.AVAILABLE
    
    def borrow_book(self):
        """Borrow a copy of the book"""
        if not self.is_available():
            raise ValueError("Book is not available for borrowing")
        
        self.available_copies -= 1
        if self.available_copies == 0:
            self.status = BookStatus.CHECKED_OUT
        self.updated_at = datetime.now()
    
    def return_book(self):
        """Return a copy of the book"""
        if self.available_copies >= self.total_copies:
            raise ValueError("All copies are already returned")
        
        self.available_copies += 1
        if self.status == BookStatus.CHECKED_OUT:
            self.status = BookStatus.AVAILABLE
        self.updated_at = datetime.now()


class Member(Base):
    """Member model representing library members"""
    
    __tablename__ = "members"
    
    id = Column(Integer, primary_key=True, index=True)
    first_name = Column(String(50), nullable=False)
    last_name = Column(String(50), nullable=False)
    email = Column(String(100), unique=True, nullable=False, index=True)
    phone = Column(String(20), nullable=False)
    address = Column(Text, nullable=False)
    membership_number = Column(String(20), unique=True, nullable=False, index=True)
    membership_date = Column(Date, nullable=False, default=date.today)
    membership_expiry = Column(Date, nullable=False)
    membership_type = Column(Enum(MembershipType), nullable=False, default=MembershipType.GENERAL)
    status = Column(Enum(MemberStatus), nullable=False, default=MemberStatus.ACTIVE)
    books_borrowed = Column(Integer, nullable=False, default=0)
    max_books_allowed = Column(Integer, nullable=False, default=3)
    total_fine = Column(Float, nullable=False, default=0.0)
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())
    
    # Relationships
    transactions = relationship("BookTransaction", back_populates="member")
    
    def get_full_name(self) -> str:
        """Get member's full name"""
        return f"{self.first_name} {self.last_name}"
    
    def can_borrow_book(self) -> bool:
        """Check if member can borrow a book"""
        return (
            self.status == MemberStatus.ACTIVE and
            self.books_borrowed < self.max_books_allowed and
            self.membership_expiry > date.today() and
            self.total_fine <= 100.0
        )
    
    def borrow_book(self):
        """Borrow a book"""
        if not self.can_borrow_book():
            raise ValueError("Member cannot borrow more books")
        
        self.books_borrowed += 1
        self.updated_at = datetime.now()
    
    def return_book(self):
        """Return a book"""
        if self.books_borrowed <= 0:
            raise ValueError("No books to return")
        
        self.books_borrowed -= 1
        self.updated_at = datetime.now()
    
    def add_fine(self, amount: float):
        """Add fine to member's account"""
        self.total_fine += amount
        if self.total_fine > 100.0:
            self.status = MemberStatus.SUSPENDED
        self.updated_at = datetime.now()
    
    def pay_fine(self, amount: float):
        """Pay fine"""
        self.total_fine = max(0.0, self.total_fine - amount)
        if self.total_fine <= 100.0 and self.status == MemberStatus.SUSPENDED:
            self.status = MemberStatus.ACTIVE
        self.updated_at = datetime.now()


class BookTransaction(Base):
    """BookTransaction model representing book borrowing/returning transactions"""
    
    __tablename__ = "book_transactions"
    
    id = Column(Integer, primary_key=True, index=True)
    book_id = Column(Integer, ForeignKey("books.id"), nullable=False)
    member_id = Column(Integer, ForeignKey("members.id"), nullable=False)
    borrow_date = Column(Date, nullable=False, default=date.today)
    due_date = Column(Date, nullable=False)
    return_date = Column(Date)
    status = Column(Enum(TransactionStatus), nullable=False, default=TransactionStatus.ACTIVE)
    fine_amount = Column(Float, default=0.0)
    fine_paid = Column(Boolean, default=False)
    renewal_count = Column(Integer, default=0)
    max_renewals = Column(Integer, default=2)
    notes = Column(Text)
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())
    
    # Relationships
    book = relationship("Book", back_populates="transactions")
    member = relationship("Member", back_populates="transactions")
    
    def is_overdue(self) -> bool:
        """Check if transaction is overdue"""
        return self.status == TransactionStatus.ACTIVE and date.today() > self.due_date
    
    def get_days_overdue(self) -> int:
        """Get number of days overdue"""
        if self.is_overdue():
            return (date.today() - self.due_date).days
        return 0
    
    def calculate_fine(self) -> float:
        """Calculate fine for overdue book"""
        if self.is_overdue():
            days_overdue = self.get_days_overdue()
            return days_overdue * 1.0  # $1 per day
        return 0.0
    
    def can_renew(self) -> bool:
        """Check if book can be renewed"""
        return (
            self.status == TransactionStatus.ACTIVE and
            self.renewal_count < self.max_renewals and
            not self.is_overdue() and
            self.book.is_available()
        )
    
    def renew_book(self):
        """Renew the book"""
        if not self.can_renew():
            raise ValueError("Book cannot be renewed")
        
        self.renewal_count += 1
        self.due_date = self.due_date + timedelta(days=14)
        self.updated_at = datetime.now()
    
    def return_book(self):
        """Return the book"""
        if self.status != TransactionStatus.ACTIVE:
            raise ValueError("Book is not currently borrowed")
        
        self.return_date = date.today()
        self.status = TransactionStatus.RETURNED
        
        # Calculate fine if overdue
        if self.is_overdue():
            self.fine_amount = self.calculate_fine()
            if self.fine_amount > 0:
                self.member.add_fine(self.fine_amount)
        
        # Update book and member
        self.book.return_book()
        self.member.return_book()
        self.updated_at = datetime.now()
    
    def mark_as_lost(self):
        """Mark book as lost"""
        if self.status != TransactionStatus.ACTIVE:
            raise ValueError("Only active transactions can be marked as lost")
        
        self.status = TransactionStatus.LOST
        self.return_date = date.today()
        self.fine_amount = self.book.price
        self.member.add_fine(self.fine_amount)
        self.member.return_book()
        self.updated_at = datetime.now() 