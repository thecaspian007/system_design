"""
Business logic services for Library Management System
"""

from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from typing import List, Optional
from datetime import date, datetime, timedelta
import uuid

from models import Book, Member, BookTransaction, BookStatus, MemberStatus, TransactionStatus, MembershipType
from schemas import BookCreate, BookUpdate, MemberCreate, MemberUpdate


class BookService:
    """Service class for book operations"""
    
    def create_book(self, db: Session, book: BookCreate) -> Book:
        """Create a new book"""
        # Generate unique ISBN if not provided
        if not book.isbn:
            book.isbn = str(uuid.uuid4())[:13]
        
        # Check if book with same ISBN already exists
        existing_book = db.query(Book).filter(Book.isbn == book.isbn).first()
        if existing_book:
            raise ValueError(f"Book with ISBN {book.isbn} already exists")
        
        db_book = Book(
            title=book.title,
            author=book.author,
            isbn=book.isbn,
            genre=book.genre,
            publication_date=book.publication_date,
            total_copies=book.total_copies,
            available_copies=book.total_copies,
            price=book.price,
            description=book.description
        )
        
        db.add(db_book)
        db.commit()
        db.refresh(db_book)
        return db_book
    
    def get_books(self, db: Session, skip: int = 0, limit: int = 100, 
                  title: Optional[str] = None, author: Optional[str] = None, 
                  genre: Optional[str] = None) -> List[Book]:
        """Get books with optional filters"""
        query = db.query(Book)
        
        if title:
            query = query.filter(Book.title.ilike(f"%{title}%"))
        if author:
            query = query.filter(Book.author.ilike(f"%{author}%"))
        if genre:
            query = query.filter(Book.genre.ilike(f"%{genre}%"))
        
        return query.offset(skip).limit(limit).all()
    
    def get_book(self, db: Session, book_id: int) -> Optional[Book]:
        """Get book by ID"""
        return db.query(Book).filter(Book.id == book_id).first()
    
    def get_book_by_isbn(self, db: Session, isbn: str) -> Optional[Book]:
        """Get book by ISBN"""
        return db.query(Book).filter(Book.isbn == isbn).first()
    
    def update_book(self, db: Session, book_id: int, book: BookUpdate) -> Optional[Book]:
        """Update book"""
        db_book = self.get_book(db, book_id)
        if not db_book:
            return None
        
        update_data = book.dict(exclude_unset=True)
        for key, value in update_data.items():
            setattr(db_book, key, value)
        
        db.commit()
        db.refresh(db_book)
        return db_book
    
    def delete_book(self, db: Session, book_id: int) -> bool:
        """Delete book"""
        db_book = self.get_book(db, book_id)
        if not db_book:
            return False
        
        # Check if book has active transactions
        active_transactions = db.query(BookTransaction).filter(
            and_(
                BookTransaction.book_id == book_id,
                BookTransaction.status == TransactionStatus.ACTIVE
            )
        ).count()
        
        if active_transactions > 0:
            raise ValueError("Cannot delete book with active transactions")
        
        db.delete(db_book)
        db.commit()
        return True


class MemberService:
    """Service class for member operations"""
    
    def create_member(self, db: Session, member: MemberCreate) -> Member:
        """Create a new member"""
        # Check if member with same email already exists
        existing_member = db.query(Member).filter(Member.email == member.email).first()
        if existing_member:
            raise ValueError(f"Member with email {member.email} already exists")
        
        # Generate unique membership number
        membership_number = f"LIB{str(uuid.uuid4())[:8].upper()}"
        
        # Set max books allowed based on membership type
        max_books_map = {
            MembershipType.STUDENT: 5,
            MembershipType.FACULTY: 10,
            MembershipType.STAFF: 7,
            MembershipType.GENERAL: 3
        }
        
        db_member = Member(
            first_name=member.first_name,
            last_name=member.last_name,
            email=member.email,
            phone=member.phone,
            address=member.address,
            membership_number=membership_number,
            membership_type=member.membership_type,
            membership_expiry=date.today() + timedelta(days=365),
            max_books_allowed=max_books_map[member.membership_type]
        )
        
        db.add(db_member)
        db.commit()
        db.refresh(db_member)
        return db_member
    
    def get_members(self, db: Session, skip: int = 0, limit: int = 100,
                   membership_type: Optional[str] = None, 
                   status: Optional[str] = None) -> List[Member]:
        """Get members with optional filters"""
        query = db.query(Member)
        
        if membership_type:
            query = query.filter(Member.membership_type == membership_type)
        if status:
            query = query.filter(Member.status == status)
        
        return query.offset(skip).limit(limit).all()
    
    def get_member(self, db: Session, member_id: int) -> Optional[Member]:
        """Get member by ID"""
        return db.query(Member).filter(Member.id == member_id).first()
    
    def get_member_by_membership_number(self, db: Session, membership_number: str) -> Optional[Member]:
        """Get member by membership number"""
        return db.query(Member).filter(Member.membership_number == membership_number).first()
    
    def update_member(self, db: Session, member_id: int, member: MemberUpdate) -> Optional[Member]:
        """Update member"""
        db_member = self.get_member(db, member_id)
        if not db_member:
            return None
        
        update_data = member.dict(exclude_unset=True)
        for key, value in update_data.items():
            setattr(db_member, key, value)
        
        db.commit()
        db.refresh(db_member)
        return db_member
    
    def delete_member(self, db: Session, member_id: int) -> bool:
        """Delete member"""
        db_member = self.get_member(db, member_id)
        if not db_member:
            return False
        
        # Check if member has active transactions
        active_transactions = db.query(BookTransaction).filter(
            and_(
                BookTransaction.member_id == member_id,
                BookTransaction.status == TransactionStatus.ACTIVE
            )
        ).count()
        
        if active_transactions > 0:
            raise ValueError("Cannot delete member with active transactions")
        
        db.delete(db_member)
        db.commit()
        return True


class TransactionService:
    """Service class for transaction operations"""
    
    def __init__(self):
        self.book_service = BookService()
        self.member_service = MemberService()
    
    def borrow_book(self, db: Session, book_id: int, member_id: int) -> BookTransaction:
        """Borrow a book"""
        # Get book and member
        book = self.book_service.get_book(db, book_id)
        member = self.member_service.get_member(db, member_id)
        
        if not book:
            raise ValueError("Book not found")
        if not member:
            raise ValueError("Member not found")
        
        # Check if book is available
        if not book.is_available():
            raise ValueError("Book is not available")
        
        # Check if member can borrow
        if not member.can_borrow_book():
            raise ValueError("Member cannot borrow book")
        
        # Check if member already has this book
        existing_transaction = db.query(BookTransaction).filter(
            and_(
                BookTransaction.book_id == book_id,
                BookTransaction.member_id == member_id,
                BookTransaction.status == TransactionStatus.ACTIVE
            )
        ).first()
        
        if existing_transaction:
            raise ValueError("Member already has this book")
        
        # Create transaction
        transaction = BookTransaction(
            book_id=book_id,
            member_id=member_id,
            borrow_date=date.today(),
            due_date=date.today() + timedelta(days=14)
        )
        
        # Update book and member
        book.borrow_book()
        member.borrow_book()
        
        db.add(transaction)
        db.commit()
        db.refresh(transaction)
        return transaction
    
    def return_book(self, db: Session, transaction_id: int) -> BookTransaction:
        """Return a book"""
        transaction = db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        
        if not transaction:
            raise ValueError("Transaction not found")
        
        if transaction.status != TransactionStatus.ACTIVE:
            raise ValueError("Transaction is not active")
        
        # Calculate fine if overdue
        if transaction.is_overdue():
            fine_amount = transaction.calculate_fine()
            transaction.fine_amount = fine_amount
            transaction.member.add_fine(fine_amount)
        
        # Update transaction
        transaction.return_date = date.today()
        transaction.status = TransactionStatus.RETURNED
        
        # Update book and member
        transaction.book.return_book()
        transaction.member.return_book()
        
        db.commit()
        db.refresh(transaction)
        return transaction
    
    def renew_book(self, db: Session, transaction_id: int) -> BookTransaction:
        """Renew a book"""
        transaction = db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        
        if not transaction:
            raise ValueError("Transaction not found")
        
        if not transaction.can_renew():
            raise ValueError("Book cannot be renewed")
        
        # Renew book
        transaction.renew_book()
        
        db.commit()
        db.refresh(transaction)
        return transaction
    
    def mark_book_lost(self, db: Session, transaction_id: int) -> BookTransaction:
        """Mark a book as lost"""
        transaction = db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        
        if not transaction:
            raise ValueError("Transaction not found")
        
        transaction.mark_as_lost()
        
        db.commit()
        db.refresh(transaction)
        return transaction
    
    def mark_book_damaged(self, db: Session, transaction_id: int, notes: str) -> BookTransaction:
        """Mark a book as damaged"""
        transaction = db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        
        if not transaction:
            raise ValueError("Transaction not found")
        
        transaction.mark_as_damaged(notes)
        
        db.commit()
        db.refresh(transaction)
        return transaction
    
    def get_transactions(self, db: Session, skip: int = 0, limit: int = 100,
                        member_id: Optional[int] = None, book_id: Optional[int] = None,
                        status: Optional[str] = None) -> List[BookTransaction]:
        """Get transactions with optional filters"""
        query = db.query(BookTransaction)
        
        if member_id:
            query = query.filter(BookTransaction.member_id == member_id)
        if book_id:
            query = query.filter(BookTransaction.book_id == book_id)
        if status:
            query = query.filter(BookTransaction.status == status)
        
        return query.offset(skip).limit(limit).all()
    
    def get_transaction(self, db: Session, transaction_id: int) -> Optional[BookTransaction]:
        """Get transaction by ID"""
        return db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
    
    def get_overdue_transactions(self, db: Session) -> List[BookTransaction]:
        """Get overdue transactions"""
        return db.query(BookTransaction).filter(
            and_(
                BookTransaction.status == TransactionStatus.ACTIVE,
                BookTransaction.due_date < date.today()
            )
        ).all()
    
    def get_member_transactions(self, db: Session, member_id: int) -> List[BookTransaction]:
        """Get transactions for a specific member"""
        return db.query(BookTransaction).filter(BookTransaction.member_id == member_id).all()
    
    def get_book_transactions(self, db: Session, book_id: int) -> List[BookTransaction]:
        """Get transactions for a specific book"""
        return db.query(BookTransaction).filter(BookTransaction.book_id == book_id).all()
    
    def pay_fine(self, db: Session, member_id: int, amount: float) -> bool:
        """Pay fine for a member"""
        member = self.member_service.get_member(db, member_id)
        if not member:
            raise ValueError("Member not found")
        
        member.pay_fine(amount)
        db.commit()
        return True
    
    def get_statistics(self, db: Session) -> dict:
        """Get library statistics"""
        total_books = db.query(Book).count()
        total_members = db.query(Member).count()
        total_transactions = db.query(BookTransaction).count()
        
        books_borrowed = db.query(Book).filter(Book.available_copies < Book.total_copies).count()
        books_available = db.query(Book).filter(Book.available_copies > 0).count()
        
        overdue_books = db.query(BookTransaction).filter(
            and_(
                BookTransaction.status == TransactionStatus.ACTIVE,
                BookTransaction.due_date < date.today()
            )
        ).count()
        
        total_fines = db.query(Member).with_entities(Member.total_fine).all()
        total_fines_amount = sum(fine[0] for fine in total_fines)
        
        active_members = db.query(Member).filter(Member.status == MemberStatus.ACTIVE).count()
        
        return {
            "total_books": total_books,
            "total_members": total_members,
            "total_transactions": total_transactions,
            "books_borrowed": books_borrowed,
            "books_available": books_available,
            "overdue_books": overdue_books,
            "total_fines": total_fines_amount,
            "active_members": active_members
        } 