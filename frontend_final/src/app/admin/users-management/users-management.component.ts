import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { User, UserRole, UserStatus } from './users-managementnew.model';
import { UserService } from './users-managementnew.service';
import { UserDialogComponent } from './user-dialog.component';

@Component({
  selector: 'app-users-management',
  templateUrl: './users-management.component.html',
  styleUrls: ['./users-management.component.scss']
})
export class UserManagementComponent implements OnInit, AfterViewInit {
  loading = true;
  filterForm: FormGroup;

  displayedColumns = [
    'userId',
    'fullName',
    'email',
    'userName',
    'phone',
    'drivingLicenseNumber',
    'role',
    'status',
    'actions'
  ];

  dataSource = new MatTableDataSource<User>([]);
  roles: UserRole[] = ['ADMIN', 'CUSTOMER'];
  statuses: UserStatus[] = ['ACTIVE', 'BLOCKED'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private fb: FormBuilder,
    private svc: UserService,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      query: [''],
      role: [''],
      status: ['']
    });

    // Filtering logic for the table
    this.dataSource.filterPredicate = (data: User, raw: string): boolean => {
      const f = JSON.parse(raw || '{}');
      const q = (f.query || '').toLowerCase().trim();

      const roleOk = !f.role || data.role === f.role;
      const statusOk = !f.status || data.status === f.status;

      const matchesQuery =
        !q ||
        (data.fullName?.toLowerCase().includes(q) ?? false) ||
        (data.email?.toLowerCase().includes(q) ?? false) ||
        (data.phone?.toLowerCase().includes(q) ?? false) ||
        (data.drivingLicenseNumber?.toLowerCase().includes(q) ?? false) ||         
        (data.userName?.toLowerCase().includes(q) ?? false);

      return roleOk && statusOk && matchesQuery;
    };
  }

  ngOnInit(): void {
    this.loadInitial();

    // live filtering
    this.filterForm.valueChanges.subscribe(v => {
      this.dataSource.filter = JSON.stringify(v || {});
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  /** Load users (default load only CUSTOMERS) */
  loadInitial(): void {
    this.loading = true;
    this.svc.search('CUSTOMER').subscribe({
      next: rows => {
        this.dataSource.data = rows;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Failed to load users', 'OK', { duration: 2500 });
      }
    });
  }

  /** Reset filters */
  resetFilters(): void {
    this.filterForm.reset({ query: '', role: '', status: '' });
    this.loadInitial();
  }

  // /** Search by keyword (backend search) */
  // applySearch(): void {
  //   const keyword = this.filterForm.get('query')?.value?.trim() || 'CUSTOMER';
  //   this.svc.search(keyword).subscribe({
  //     next: rows => (this.dataSource.data = rows),
  //     error: () => this.snack.open('Search failed', 'OK', { duration: 2000 })
  //   });
  // }

  /** Filter by status (backend search) */
  applyStatus(): void {
    const status = this.filterForm.get('status')?.value;
    if (!status) return;
    this.svc.search(status.toUpperCase()).subscribe({
      next: rows => (this.dataSource.data = rows),
      error: () => this.snack.open('Filter by status failed', 'OK', { duration: 2000 })
    });
  }

  /** Toggle ACTIVE / BLOCKED */
  toggleStatus(row: User): void {
  if (!row.userId) return;
  this.svc.toggleStatus(row.userId).subscribe({
    next: msg => {
      // parse backend string to detect status
      if (msg.includes('BLOCKED')) {
        row.status = 'BLOCKED';
      } else if (msg.includes('ACTIVE')) {
        row.status = 'ACTIVE';
      }
      this.snack.open(msg, 'OK', { duration: 2000 });
    },
    error: () => this.snack.open('Failed to change status', 'OK', { duration: 2000 })
  });
}


  /** Delete user */
  delete(row: User): void {
    if (!row.userId) return;
    if (confirm(`Delete user "${row.fullName}"? This cannot be undone.`)) {
      this.svc.delete(row.userId).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(x => x.userId !== row.userId);
          this.snack.open('User deleted', 'OK', { duration: 1800 });
          this.loadInitial();
        },
        error: () => this.snack.open('Failed to delete user', 'OK', { duration: 2000 })
      });
    }
  }

  /** Export table to CSV */
  exportCsv(): void {
    const rows = this.dataSource.filteredData.length ? this.dataSource.filteredData : this.dataSource.data;
    const header = ['ID','Name','Email','UserName','Phone','DrivingLicence','Role','Status'];
    const body = rows.map(r => [
      r.userId,
      r.fullName,
      r.email,
      r.userName,
      r.phone || '',
      r.drivingLicenseNumber || '',
      r.role,
      r.status
    ]);
    const csv = [header, ...body]
      .map(r => r.map(x => `"${(x ?? '').toString().replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `users-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(a.href);
  }
}
