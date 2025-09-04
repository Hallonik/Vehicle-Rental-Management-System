import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Feedback } from './feedback.model';
import { FeedbackService } from './feedback.service';

@Component({
  selector: 'app-feedback',
  standalone: false,
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.scss']
})
export class FeedbackComponent implements OnInit {
  displayedColumns = ['id','name', 'email', 'rating', 'message', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<Feedback>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private svc: FeedbackService) {}

  ngOnInit(): void {
    this.loadFeedback();
  }

  loadFeedback(): void {
    this.svc.list$().subscribe({
      next: list => {
        this.dataSource.data = list;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: err => console.error('Error fetching feedback', err)
    });
  }

  remove(id: number): void {
    if (confirm('Delete this feedback?')) {
      this.svc.remove(id).subscribe({
        next: () => this.loadFeedback(),
        error: err => console.error('Error deleting feedback', err)
      });
    }
  }
}
