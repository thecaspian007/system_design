"""
Library Management System - CLI Version

This is a command-line interface for the library management system.
Run this file directly to interact with the system.

@author System Design Repository
"""

import sys
import os
from datetime import date, timedelta

# Add the current directory to the Python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from models import *
from database import engine, SessionLocal


class LibraryManagementSystem:
    """
    Library Management System - CLI Application
    
    This system demonstrates:
    - Book management (add, search, update)
    - Member management (register, search, update)
    - Book borrowing and returning
    - Fine calculation and payment
    - Transaction history
    
    @author System Design Repository
    """
    
    def __init__(self):
        # Create database tables
        Base.metadata.create_all(bind=engine)
        
        # Create database session
        self.db = SessionLocal()
        
        # Initialize with sample data
        self._initialize_sample_data()
    
    def _initialize_sample_data(self):
        """Initialize system with sample data"""
        # Check if data already exists
        if self.db.query(Book).count() > 0:
            return
        
        # Sample books
        books = [
            Book(
                title="To Kill a Mockingbird",
                author="Harper Lee",
                isbn="978-0-06-112008-4",
                genre="Fiction",
                publication_date=date(1960, 7, 11),
                total_copies=3,
                available_copies=3,
                price=12.99,
                description="A gripping tale of racial injustice and childhood innocence"
            ),
            Book(
                title="1984",
                author="George Orwell",
                isbn="978-0-452-28423-4",
                genre="Dystopian",
                publication_date=date(1949, 6, 8),
                total_copies=2,
                available_copies=2,
                price=13.99,
                description="A dystopian social science fiction novel"
            ),
            Book(
                title="Python Crash Course",
                author="Eric Matthes",
                isbn="978-1-59327-928-8",
                genre="Programming",
                publication_date=date(2019, 5, 3),
                total_copies=5,
                available_copies=5,
                price=29.99,
                description="A hands-on introduction to programming"
            ),
            Book(
                title="The Great Gatsby",
                author="F. Scott Fitzgerald",
                isbn="978-0-7432-7356-5",
                genre="Fiction",
                publication_date=date(1925, 4, 10),
                total_copies=2,
                available_copies=2,
                price=10.99,
                description="A classic American novel"
            )
        ]
        
        for book in books:
            self.db.add(book)
        
        # Sample members
        members = [
            Member(
                first_name="John",
                last_name="Doe",
                email="john.doe@email.com",
                phone="555-0101",
                address="123 Main St, Anytown, USA",
                membership_number="LIB001",
                membership_type=MembershipType.GENERAL,
                membership_expiry=date.today() + timedelta(days=365),
                max_books_allowed=3
            ),
            Member(
                first_name="Jane",
                last_name="Smith",
                email="jane.smith@email.com",
                phone="555-0102",
                address="456 Oak Ave, Anytown, USA",
                membership_number="LIB002",
                membership_type=MembershipType.STUDENT,
                membership_expiry=date.today() + timedelta(days=365),
                max_books_allowed=5
            ),
            Member(
                first_name="Bob",
                last_name="Johnson",
                email="bob.johnson@email.com",
                phone="555-0103",
                address="789 Pine St, Anytown, USA",
                membership_number="LIB003",
                membership_type=MembershipType.FACULTY,
                membership_expiry=date.today() + timedelta(days=365),
                max_books_allowed=10
            )
        ]
        
        for member in members:
            self.db.add(member)
        
        self.db.commit()
        print("Sample data initialized!")
    
    def start(self):
        """Start the library management system"""
        print("=== Welcome to Library Management System ===")
        
        while True:
            try:
                self._show_main_menu()
                choice = self._get_int_input()
                
                if choice == 1:
                    self._book_management()
                elif choice == 2:
                    self._member_management()
                elif choice == 3:
                    self._transaction_management()
                elif choice == 4:
                    self._reports()
                elif choice == 5:
                    print("Thank you for using Library Management System!")
                    break
                else:
                    print("Invalid choice. Please try again.")
                    
            except KeyboardInterrupt:
                print("\nThank you for using Library Management System!")
                break
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_main_menu(self):
        """Show main menu"""
        print("\n--- Main Menu ---")
        print("1. Book Management")
        print("2. Member Management")
        print("3. Transaction Management")
        print("4. Reports")
        print("5. Exit")
        print("Choose an option: ", end="")
    
    def _book_management(self):
        """Book management menu"""
        while True:
            print("\n--- Book Management ---")
            print("1. Add Book")
            print("2. Search Books")
            print("3. View All Books")
            print("4. Update Book")
            print("5. Delete Book")
            print("6. Back to Main Menu")
            
            choice = self._get_int_input("Choose an option: ")
            
            if choice == 1:
                self._add_book()
            elif choice == 2:
                self._search_books()
            elif choice == 3:
                self._view_all_books()
            elif choice == 4:
                self._update_book()
            elif choice == 5:
                self._delete_book()
            elif choice == 6:
                break
            else:
                print("Invalid choice. Please try again.")
    
    def _add_book(self):
        """Add a new book"""
        print("\n--- Add Book ---")
        title = input("Enter book title: ")
        author = input("Enter author name: ")
        isbn = input("Enter ISBN: ")
        genre = input("Enter genre: ")
        price = self._get_float_input("Enter price: $")
        total_copies = self._get_int_input("Enter total copies: ")
        description = input("Enter description (optional): ")
        
        book = Book(
            title=title,
            author=author,
            isbn=isbn,
            genre=genre,
            price=price,
            total_copies=total_copies,
            available_copies=total_copies,
            description=description
        )
        
        try:
            self.db.add(book)
            self.db.commit()
            print("Book added successfully!")
        except Exception as e:
            self.db.rollback()
            print(f"Error adding book: {e}")
    
    def _search_books(self):
        """Search books"""
        print("\n--- Search Books ---")
        print("1. Search by Title")
        print("2. Search by Author")
        print("3. Search by ISBN")
        print("4. Search by Genre")
        
        choice = self._get_int_input("Choose search option: ")
        
        if choice == 1:
            title = input("Enter title to search: ")
            books = self.db.query(Book).filter(Book.title.ilike(f"%{title}%")).all()
        elif choice == 2:
            author = input("Enter author to search: ")
            books = self.db.query(Book).filter(Book.author.ilike(f"%{author}%")).all()
        elif choice == 3:
            isbn = input("Enter ISBN to search: ")
            books = self.db.query(Book).filter(Book.isbn == isbn).all()
        elif choice == 4:
            genre = input("Enter genre to search: ")
            books = self.db.query(Book).filter(Book.genre.ilike(f"%{genre}%")).all()
        else:
            print("Invalid choice.")
            return
        
        self._display_books(books)
    
    def _view_all_books(self):
        """View all books"""
        print("\n--- All Books ---")
        books = self.db.query(Book).all()
        self._display_books(books)
    
    def _display_books(self, books):
        """Display books in a formatted way"""
        if not books:
            print("No books found.")
            return
        
        print(f"{'ID':<5} {'Title':<25} {'Author':<20} {'Genre':<15} {'Available/Total':<15} {'Price':<10}")
        print("-" * 90)
        
        for book in books:
            print(f"{book.id:<5} {book.title[:24]:<25} {book.author[:19]:<20} {book.genre[:14]:<15} {book.available_copies}/{book.total_copies:<15} ${book.price:<10.2f}")
    
    def _update_book(self):
        """Update a book"""
        print("\n--- Update Book ---")
        book_id = self._get_int_input("Enter book ID to update: ")
        
        book = self.db.query(Book).filter(Book.id == book_id).first()
        if not book:
            print("Book not found.")
            return
        
        print(f"Current title: {book.title}")
        new_title = input("Enter new title (or press Enter to keep current): ")
        if new_title:
            book.title = new_title
        
        print(f"Current author: {book.author}")
        new_author = input("Enter new author (or press Enter to keep current): ")
        if new_author:
            book.author = new_author
        
        print(f"Current price: ${book.price}")
        new_price = input("Enter new price (or press Enter to keep current): ")
        if new_price:
            book.price = float(new_price)
        
        try:
            self.db.commit()
            print("Book updated successfully!")
        except Exception as e:
            self.db.rollback()
            print(f"Error updating book: {e}")
    
    def _delete_book(self):
        """Delete a book"""
        print("\n--- Delete Book ---")
        book_id = self._get_int_input("Enter book ID to delete: ")
        
        book = self.db.query(Book).filter(Book.id == book_id).first()
        if not book:
            print("Book not found.")
            return
        
        # Check for active transactions
        active_transactions = self.db.query(BookTransaction).filter(
            BookTransaction.book_id == book_id,
            BookTransaction.status == TransactionStatus.ACTIVE
        ).count()
        
        if active_transactions > 0:
            print("Cannot delete book with active transactions.")
            return
        
        confirm = input(f"Are you sure you want to delete '{book.title}'? (y/N): ")
        if confirm.lower() == 'y':
            try:
                self.db.delete(book)
                self.db.commit()
                print("Book deleted successfully!")
            except Exception as e:
                self.db.rollback()
                print(f"Error deleting book: {e}")
    
    def _member_management(self):
        """Member management menu"""
        while True:
            print("\n--- Member Management ---")
            print("1. Register Member")
            print("2. Search Members")
            print("3. View All Members")
            print("4. Update Member")
            print("5. Pay Fine")
            print("6. Back to Main Menu")
            
            choice = self._get_int_input("Choose an option: ")
            
            if choice == 1:
                self._register_member()
            elif choice == 2:
                self._search_members()
            elif choice == 3:
                self._view_all_members()
            elif choice == 4:
                self._update_member()
            elif choice == 5:
                self._pay_fine()
            elif choice == 6:
                break
            else:
                print("Invalid choice. Please try again.")
    
    def _register_member(self):
        """Register a new member"""
        print("\n--- Register Member ---")
        first_name = input("Enter first name: ")
        last_name = input("Enter last name: ")
        email = input("Enter email: ")
        phone = input("Enter phone: ")
        address = input("Enter address: ")
        
        print("Select membership type:")
        print("1. General (3 books)")
        print("2. Student (5 books)")
        print("3. Faculty (10 books)")
        print("4. Staff (7 books)")
        
        type_choice = self._get_int_input("Choose membership type: ")
        membership_types = {
            1: MembershipType.GENERAL,
            2: MembershipType.STUDENT,
            3: MembershipType.FACULTY,
            4: MembershipType.STAFF
        }
        
        if type_choice not in membership_types:
            print("Invalid membership type.")
            return
        
        membership_type = membership_types[type_choice]
        max_books_map = {
            MembershipType.GENERAL: 3,
            MembershipType.STUDENT: 5,
            MembershipType.FACULTY: 10,
            MembershipType.STAFF: 7
        }
        
        # Generate membership number
        last_member = self.db.query(Member).order_by(Member.id.desc()).first()
        next_id = (last_member.id + 1) if last_member else 1
        membership_number = f"LIB{next_id:04d}"
        
        member = Member(
            first_name=first_name,
            last_name=last_name,
            email=email,
            phone=phone,
            address=address,
            membership_number=membership_number,
            membership_type=membership_type,
            membership_expiry=date.today() + timedelta(days=365),
            max_books_allowed=max_books_map[membership_type]
        )
        
        try:
            self.db.add(member)
            self.db.commit()
            print(f"Member registered successfully! Membership Number: {membership_number}")
        except Exception as e:
            self.db.rollback()
            print(f"Error registering member: {e}")
    
    def _search_members(self):
        """Search members"""
        print("\n--- Search Members ---")
        print("1. Search by Name")
        print("2. Search by Email")
        print("3. Search by Membership Number")
        
        choice = self._get_int_input("Choose search option: ")
        
        if choice == 1:
            name = input("Enter name to search: ")
            members = self.db.query(Member).filter(
                (Member.first_name.ilike(f"%{name}%")) |
                (Member.last_name.ilike(f"%{name}%"))
            ).all()
        elif choice == 2:
            email = input("Enter email to search: ")
            members = self.db.query(Member).filter(Member.email.ilike(f"%{email}%")).all()
        elif choice == 3:
            membership_number = input("Enter membership number: ")
            members = self.db.query(Member).filter(Member.membership_number == membership_number).all()
        else:
            print("Invalid choice.")
            return
        
        self._display_members(members)
    
    def _view_all_members(self):
        """View all members"""
        print("\n--- All Members ---")
        members = self.db.query(Member).all()
        self._display_members(members)
    
    def _display_members(self, members):
        """Display members in a formatted way"""
        if not members:
            print("No members found.")
            return
        
        print(f"{'ID':<5} {'Name':<20} {'Email':<25} {'Membership#':<12} {'Type':<10} {'Books':<10} {'Fine':<10}")
        print("-" * 92)
        
        for member in members:
            full_name = f"{member.first_name} {member.last_name}"
            print(f"{member.id:<5} {full_name[:19]:<20} {member.email[:24]:<25} {member.membership_number:<12} {member.membership_type.value:<10} {member.books_borrowed}/{member.max_books_allowed:<10} ${member.total_fine:<10.2f}")
    
    def _update_member(self):
        """Update a member"""
        print("\n--- Update Member ---")
        member_id = self._get_int_input("Enter member ID to update: ")
        
        member = self.db.query(Member).filter(Member.id == member_id).first()
        if not member:
            print("Member not found.")
            return
        
        print(f"Current email: {member.email}")
        new_email = input("Enter new email (or press Enter to keep current): ")
        if new_email:
            member.email = new_email
        
        print(f"Current phone: {member.phone}")
        new_phone = input("Enter new phone (or press Enter to keep current): ")
        if new_phone:
            member.phone = new_phone
        
        print(f"Current address: {member.address}")
        new_address = input("Enter new address (or press Enter to keep current): ")
        if new_address:
            member.address = new_address
        
        try:
            self.db.commit()
            print("Member updated successfully!")
        except Exception as e:
            self.db.rollback()
            print(f"Error updating member: {e}")
    
    def _pay_fine(self):
        """Pay fine for a member"""
        print("\n--- Pay Fine ---")
        member_id = self._get_int_input("Enter member ID: ")
        
        member = self.db.query(Member).filter(Member.id == member_id).first()
        if not member:
            print("Member not found.")
            return
        
        print(f"Current fine: ${member.total_fine:.2f}")
        if member.total_fine <= 0:
            print("No fine to pay.")
            return
        
        amount = self._get_float_input("Enter amount to pay: $")
        if amount <= 0:
            print("Invalid amount.")
            return
        
        try:
            member.pay_fine(amount)
            self.db.commit()
            print(f"Fine payment successful! Remaining fine: ${member.total_fine:.2f}")
        except Exception as e:
            self.db.rollback()
            print(f"Error processing payment: {e}")
    
    def _transaction_management(self):
        """Transaction management menu"""
        while True:
            print("\n--- Transaction Management ---")
            print("1. Borrow Book")
            print("2. Return Book")
            print("3. Renew Book")
            print("4. View Active Transactions")
            print("5. View Transaction History")
            print("6. View Overdue Books")
            print("7. Back to Main Menu")
            
            choice = self._get_int_input("Choose an option: ")
            
            if choice == 1:
                self._borrow_book()
            elif choice == 2:
                self._return_book()
            elif choice == 3:
                self._renew_book()
            elif choice == 4:
                self._view_active_transactions()
            elif choice == 5:
                self._view_transaction_history()
            elif choice == 6:
                self._view_overdue_books()
            elif choice == 7:
                break
            else:
                print("Invalid choice. Please try again.")
    
    def _borrow_book(self):
        """Borrow a book"""
        print("\n--- Borrow Book ---")
        book_id = self._get_int_input("Enter book ID: ")
        member_id = self._get_int_input("Enter member ID: ")
        
        book = self.db.query(Book).filter(Book.id == book_id).first()
        member = self.db.query(Member).filter(Member.id == member_id).first()
        
        if not book:
            print("Book not found.")
            return
        
        if not member:
            print("Member not found.")
            return
        
        if not book.is_available():
            print("Book is not available for borrowing.")
            return
        
        if not member.can_borrow_book():
            print("Member cannot borrow book. Check membership status, fine, or book limit.")
            return
        
        # Check if member already has this book
        existing_transaction = self.db.query(BookTransaction).filter(
            BookTransaction.book_id == book_id,
            BookTransaction.member_id == member_id,
            BookTransaction.status == TransactionStatus.ACTIVE
        ).first()
        
        if existing_transaction:
            print("Member already has this book.")
            return
        
        try:
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
            
            self.db.add(transaction)
            self.db.commit()
            
            print("Book borrowed successfully!")
            print(f"Due date: {transaction.due_date}")
            
        except Exception as e:
            self.db.rollback()
            print(f"Error borrowing book: {e}")
    
    def _return_book(self):
        """Return a book"""
        print("\n--- Return Book ---")
        transaction_id = self._get_int_input("Enter transaction ID: ")
        
        transaction = self.db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        if not transaction:
            print("Transaction not found.")
            return
        
        if transaction.status != TransactionStatus.ACTIVE:
            print("Transaction is not active.")
            return
        
        try:
            # Calculate fine if overdue
            if transaction.is_overdue():
                fine_amount = transaction.calculate_fine()
                print(f"Book is overdue by {transaction.get_days_overdue()} days. Fine: ${fine_amount:.2f}")
                transaction.fine_amount = fine_amount
                transaction.member.add_fine(fine_amount)
            
            # Update transaction
            transaction.return_date = date.today()
            transaction.status = TransactionStatus.RETURNED
            
            # Update book and member
            transaction.book.return_book()
            transaction.member.return_book()
            
            self.db.commit()
            
            print("Book returned successfully!")
            if transaction.fine_amount > 0:
                print(f"Fine added: ${transaction.fine_amount:.2f}")
            
        except Exception as e:
            self.db.rollback()
            print(f"Error returning book: {e}")
    
    def _renew_book(self):
        """Renew a book"""
        print("\n--- Renew Book ---")
        transaction_id = self._get_int_input("Enter transaction ID: ")
        
        transaction = self.db.query(BookTransaction).filter(BookTransaction.id == transaction_id).first()
        if not transaction:
            print("Transaction not found.")
            return
        
        if not transaction.can_renew():
            print("Book cannot be renewed. Check if it's overdue or renewal limit reached.")
            return
        
        try:
            old_due_date = transaction.due_date
            transaction.renew_book()
            self.db.commit()
            
            print("Book renewed successfully!")
            print(f"Old due date: {old_due_date}")
            print(f"New due date: {transaction.due_date}")
            print(f"Renewals used: {transaction.renewal_count}/{transaction.max_renewals}")
            
        except Exception as e:
            self.db.rollback()
            print(f"Error renewing book: {e}")
    
    def _view_active_transactions(self):
        """View active transactions"""
        print("\n--- Active Transactions ---")
        transactions = self.db.query(BookTransaction).filter(
            BookTransaction.status == TransactionStatus.ACTIVE
        ).all()
        
        self._display_transactions(transactions)
    
    def _view_transaction_history(self):
        """View transaction history"""
        print("\n--- Transaction History ---")
        transactions = self.db.query(BookTransaction).order_by(BookTransaction.created_at.desc()).all()
        
        self._display_transactions(transactions)
    
    def _view_overdue_books(self):
        """View overdue books"""
        print("\n--- Overdue Books ---")
        transactions = self.db.query(BookTransaction).filter(
            BookTransaction.status == TransactionStatus.ACTIVE,
            BookTransaction.due_date < date.today()
        ).all()
        
        if not transactions:
            print("No overdue books.")
            return
        
        self._display_transactions(transactions)
    
    def _display_transactions(self, transactions):
        """Display transactions in a formatted way"""
        if not transactions:
            print("No transactions found.")
            return
        
        print(f"{'ID':<5} {'Book':<25} {'Member':<20} {'Borrow Date':<12} {'Due Date':<12} {'Status':<10} {'Fine':<10}")
        print("-" * 94)
        
        for transaction in transactions:
            book_title = transaction.book.title[:24] if transaction.book else "Unknown"
            member_name = f"{transaction.member.first_name} {transaction.member.last_name}"[:19] if transaction.member else "Unknown"
            
            print(f"{transaction.id:<5} {book_title:<25} {member_name:<20} {transaction.borrow_date:<12} {transaction.due_date:<12} {transaction.status.value:<10} ${transaction.fine_amount:<10.2f}")
    
    def _reports(self):
        """Generate reports"""
        print("\n--- Reports ---")
        
        # Basic statistics
        total_books = self.db.query(Book).count()
        total_members = self.db.query(Member).count()
        total_transactions = self.db.query(BookTransaction).count()
        
        books_borrowed = self.db.query(Book).filter(Book.available_copies < Book.total_copies).count()
        books_available = self.db.query(Book).filter(Book.available_copies > 0).count()
        
        active_transactions = self.db.query(BookTransaction).filter(
            BookTransaction.status == TransactionStatus.ACTIVE
        ).count()
        
        overdue_books = self.db.query(BookTransaction).filter(
            BookTransaction.status == TransactionStatus.ACTIVE,
            BookTransaction.due_date < date.today()
        ).count()
        
        total_fines = self.db.query(Member).with_entities(Member.total_fine).all()
        total_fines_amount = sum(fine[0] for fine in total_fines)
        
        print(f"Total Books: {total_books}")
        print(f"Total Members: {total_members}")
        print(f"Total Transactions: {total_transactions}")
        print(f"Books Currently Borrowed: {books_borrowed}")
        print(f"Books Available: {books_available}")
        print(f"Active Transactions: {active_transactions}")
        print(f"Overdue Books: {overdue_books}")
        print(f"Total Outstanding Fines: ${total_fines_amount:.2f}")
    
    def _get_int_input(self, prompt="Enter number: "):
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number.")
    
    def _get_float_input(self, prompt="Enter number: "):
        """Get float input with validation"""
        while True:
            try:
                return float(input(prompt))
            except ValueError:
                print("Please enter a valid number.")
    
    def __del__(self):
        """Close database connection"""
        if hasattr(self, 'db'):
            self.db.close()


if __name__ == "__main__":
    library_system = LibraryManagementSystem()
    library_system.start() 