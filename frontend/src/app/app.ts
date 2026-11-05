import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { Api } from './services/api';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  message = 'Loading...';

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.api.getMessage().subscribe({

      next: (response) => {

        console.log('API RESPONSE:', response);

        this.message = response;

        this.cdr.detectChanges();

      },

      error: (error) => {

        console.error('API ERROR:', error);

        this.message = 'Backend connection failed!';

        this.cdr.detectChanges();

      }

    });

  }
}