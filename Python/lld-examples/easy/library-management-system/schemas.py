"""
Pydantic schemas for Library Management System API
"""

from pydantic import BaseModel, EmailStr, validator
from typing import Optional, List
from datetime import date, datetime
from models import BookStatus, MembershipType, MemberStatus, TransactionStatus

# Book schemas
class BookBase(BaseModel):
    title: str
    author: str
    isbn: str
    genre: str
    publication_date: Optional[date] = None
    total_copies: int = 1
    price: float = 0.0
    description: Optional[str] = None

class BookCreate(BookBase):
    pass

class BookUpdate(BaseModel):
    title: Optional[str] = None
    author: Optional[str] = None
    genre: Optional[str] = None
    publication_date: Optional[date] = None
    total_copies: Optional[int] = None
    price: Optional[float] = None
    description: Optional[str] = None

class BookResponse(BookBase):
    id: int
    available_copies: int
    status: BookStatus
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True

# Member schemas
class MemberBase(BaseModel):
    first_name: str
    last_name: str
    email: EmailStr
    phone: str
    address: str
    membership_type: MembershipType = MembershipType.GENERAL

class MemberCreate(MemberBase):
    pass

class MemberUpdate(BaseModel):
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    address: Optional[str] = None
    membership_type: Optional[MembershipType] = None

class MemberResponse(MemberBase):
    id: int
    membership_number: str
    membership_date: date
    membership_expiry: date
    status: MemberStatus
    books_borrowed: int
    max_books_allowed: int
    total_fine: float
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True

# Transaction schemas
class TransactionBase(BaseModel):
    book_id: int
    member_id: int

class TransactionCreate(TransactionBase):
    pass

class TransactionResponse(BaseModel):
    id: int
    book_id: int
    member_id: int
    borrow_date: date
    due_date: date
    return_date: Optional[date] = None
    status: TransactionStatus
    fine_amount: float
    fine_paid: bool
    renewal_count: int
    max_renewals: int
    notes: Optional[str] = None
    created_at: datetime
    updated_at: datetime
    
    # Nested objects
    book: BookResponse
    member: MemberResponse
    
    class Config:
        from_attributes = True

# Request schemas
class BorrowBookRequest(BaseModel):
    book_id: int
    member_id: int

class ReturnBookRequest(BaseModel):
    transaction_id: int

class RenewBookRequest(BaseModel):
    transaction_id: int

class PayFineRequest(BaseModel):
    member_id: int
    amount: float
    
    @validator('amount')
    def amount_must_be_positive(cls, v):
        if v <= 0:
            raise ValueError('Amount must be positive')
        return v

# Response schemas
class StatisticsResponse(BaseModel):
    total_books: int
    total_members: int
    total_transactions: int
    books_borrowed: int
    books_available: int
    overdue_books: int
    total_fines: float
    active_members: int

class FineResponse(BaseModel):
    member_id: int
    total_fine: float
    fine_paid: bool 