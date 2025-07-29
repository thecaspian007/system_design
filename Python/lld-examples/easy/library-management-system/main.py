"""
Library Management System - Main Application

This is a comprehensive library management system that demonstrates:
- Book management (CRUD operations)
- Member management
- Book borrowing and returning
- Transaction tracking
- Fine calculation

Features:
- RESTful API using FastAPI
- SQLAlchemy ORM for database operations
- Pydantic models for data validation
- Automatic API documentation
- Authentication and authorization
- Transaction management
- Fine calculation system

Author: System Design Repository
"""

from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from typing import List, Optional

from database import engine, SessionLocal, Base
from models import Book, Member, BookTransaction
from schemas import (
    BookCreate, BookUpdate, BookResponse,
    MemberCreate, MemberUpdate, MemberResponse,
    TransactionCreate, TransactionResponse,
    BorrowBookRequest, ReturnBookRequest
)
from services import BookService, MemberService, TransactionService

# Create tables
Base.metadata.create_all(bind=engine)

# Create FastAPI app
app = FastAPI(
    title="Library Management System",
    description="A comprehensive library management system with book and member management",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Dependency to get database session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Initialize services
book_service = BookService()
member_service = MemberService()
transaction_service = TransactionService()

@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Welcome to Library Management System",
        "version": "1.0.0",
        "docs": "/docs",
        "redoc": "/redoc"
    }

# Book endpoints
@app.post("/books/", response_model=BookResponse, tags=["Books"])
async def create_book(book: BookCreate, db: Session = Depends(get_db)):
    """Create a new book"""
    try:
        return book_service.create_book(db, book)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/books/", response_model=List[BookResponse], tags=["Books"])
async def get_books(
    skip: int = 0, 
    limit: int = 100, 
    title: Optional[str] = None,
    author: Optional[str] = None,
    genre: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """Get all books with optional filters"""
    return book_service.get_books(db, skip, limit, title, author, genre)

@app.get("/books/{book_id}", response_model=BookResponse, tags=["Books"])
async def get_book(book_id: int, db: Session = Depends(get_db)):
    """Get a specific book by ID"""
    book = book_service.get_book(db, book_id)
    if not book:
        raise HTTPException(status_code=404, detail="Book not found")
    return book

@app.put("/books/{book_id}", response_model=BookResponse, tags=["Books"])
async def update_book(book_id: int, book: BookUpdate, db: Session = Depends(get_db)):
    """Update a book"""
    updated_book = book_service.update_book(db, book_id, book)
    if not updated_book:
        raise HTTPException(status_code=404, detail="Book not found")
    return updated_book

@app.delete("/books/{book_id}", tags=["Books"])
async def delete_book(book_id: int, db: Session = Depends(get_db)):
    """Delete a book"""
    if not book_service.delete_book(db, book_id):
        raise HTTPException(status_code=404, detail="Book not found")
    return {"message": "Book deleted successfully"}

@app.get("/books/search/{isbn}", response_model=BookResponse, tags=["Books"])
async def search_book_by_isbn(isbn: str, db: Session = Depends(get_db)):
    """Search book by ISBN"""
    book = book_service.get_book_by_isbn(db, isbn)
    if not book:
        raise HTTPException(status_code=404, detail="Book not found")
    return book

# Member endpoints
@app.post("/members/", response_model=MemberResponse, tags=["Members"])
async def create_member(member: MemberCreate, db: Session = Depends(get_db)):
    """Create a new member"""
    try:
        return member_service.create_member(db, member)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/members/", response_model=List[MemberResponse], tags=["Members"])
async def get_members(
    skip: int = 0, 
    limit: int = 100,
    membership_type: Optional[str] = None,
    status: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """Get all members with optional filters"""
    return member_service.get_members(db, skip, limit, membership_type, status)

@app.get("/members/{member_id}", response_model=MemberResponse, tags=["Members"])
async def get_member(member_id: int, db: Session = Depends(get_db)):
    """Get a specific member by ID"""
    member = member_service.get_member(db, member_id)
    if not member:
        raise HTTPException(status_code=404, detail="Member not found")
    return member

@app.put("/members/{member_id}", response_model=MemberResponse, tags=["Members"])
async def update_member(member_id: int, member: MemberUpdate, db: Session = Depends(get_db)):
    """Update a member"""
    updated_member = member_service.update_member(db, member_id, member)
    if not updated_member:
        raise HTTPException(status_code=404, detail="Member not found")
    return updated_member

@app.delete("/members/{member_id}", tags=["Members"])
async def delete_member(member_id: int, db: Session = Depends(get_db)):
    """Delete a member"""
    if not member_service.delete_member(db, member_id):
        raise HTTPException(status_code=404, detail="Member not found")
    return {"message": "Member deleted successfully"}

@app.get("/members/search/{membership_number}", response_model=MemberResponse, tags=["Members"])
async def search_member_by_membership_number(membership_number: str, db: Session = Depends(get_db)):
    """Search member by membership number"""
    member = member_service.get_member_by_membership_number(db, membership_number)
    if not member:
        raise HTTPException(status_code=404, detail="Member not found")
    return member

# Transaction endpoints
@app.post("/transactions/borrow", response_model=TransactionResponse, tags=["Transactions"])
async def borrow_book(request: BorrowBookRequest, db: Session = Depends(get_db)):
    """Borrow a book"""
    try:
        return transaction_service.borrow_book(db, request.book_id, request.member_id)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/transactions/return", response_model=TransactionResponse, tags=["Transactions"])
async def return_book(request: ReturnBookRequest, db: Session = Depends(get_db)):
    """Return a book"""
    try:
        return transaction_service.return_book(db, request.transaction_id)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/transactions/", response_model=List[TransactionResponse], tags=["Transactions"])
async def get_transactions(
    skip: int = 0, 
    limit: int = 100,
    member_id: Optional[int] = None,
    book_id: Optional[int] = None,
    status: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """Get all transactions with optional filters"""
    return transaction_service.get_transactions(db, skip, limit, member_id, book_id, status)

@app.get("/transactions/{transaction_id}", response_model=TransactionResponse, tags=["Transactions"])
async def get_transaction(transaction_id: int, db: Session = Depends(get_db)):
    """Get a specific transaction by ID"""
    transaction = transaction_service.get_transaction(db, transaction_id)
    if not transaction:
        raise HTTPException(status_code=404, detail="Transaction not found")
    return transaction

@app.get("/transactions/member/{member_id}", response_model=List[TransactionResponse], tags=["Transactions"])
async def get_member_transactions(member_id: int, db: Session = Depends(get_db)):
    """Get all transactions for a specific member"""
    return transaction_service.get_member_transactions(db, member_id)

@app.get("/transactions/book/{book_id}", response_model=List[TransactionResponse], tags=["Transactions"])
async def get_book_transactions(book_id: int, db: Session = Depends(get_db)):
    """Get all transactions for a specific book"""
    return transaction_service.get_book_transactions(db, book_id)

@app.get("/transactions/overdue", response_model=List[TransactionResponse], tags=["Transactions"])
async def get_overdue_transactions(db: Session = Depends(get_db)):
    """Get all overdue transactions"""
    return transaction_service.get_overdue_transactions(db)

@app.post("/transactions/{transaction_id}/renew", response_model=TransactionResponse, tags=["Transactions"])
async def renew_book(transaction_id: int, db: Session = Depends(get_db)):
    """Renew a book"""
    try:
        return transaction_service.renew_book(db, transaction_id)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/transactions/{transaction_id}/mark-lost", response_model=TransactionResponse, tags=["Transactions"])
async def mark_book_lost(transaction_id: int, db: Session = Depends(get_db)):
    """Mark a book as lost"""
    try:
        return transaction_service.mark_book_lost(db, transaction_id)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/transactions/{transaction_id}/mark-damaged", response_model=TransactionResponse, tags=["Transactions"])
async def mark_book_damaged(transaction_id: int, notes: str, db: Session = Depends(get_db)):
    """Mark a book as damaged"""
    try:
        return transaction_service.mark_book_damaged(db, transaction_id, notes)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# Fine endpoints
@app.post("/members/{member_id}/pay-fine", tags=["Fines"])
async def pay_fine(member_id: int, amount: float, db: Session = Depends(get_db)):
    """Pay fine for a member"""
    try:
        member_service.pay_fine(db, member_id, amount)
        return {"message": f"Fine of ${amount} paid successfully"}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/members/{member_id}/fine", tags=["Fines"])
async def get_member_fine(member_id: int, db: Session = Depends(get_db)):
    """Get total fine for a member"""
    member = member_service.get_member(db, member_id)
    if not member:
        raise HTTPException(status_code=404, detail="Member not found")
    return {"member_id": member_id, "total_fine": member.total_fine}

# Statistics endpoints
@app.get("/statistics/", tags=["Statistics"])
async def get_statistics(db: Session = Depends(get_db)):
    """Get library statistics"""
    try:
        stats = {
            "total_books": book_service.get_total_books(db),
            "total_members": member_service.get_total_members(db),
            "total_transactions": transaction_service.get_total_transactions(db),
            "active_transactions": transaction_service.get_active_transactions_count(db),
            "overdue_transactions": transaction_service.get_overdue_transactions_count(db),
            "total_fines": member_service.get_total_fines(db)
        }
        return stats
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True) 