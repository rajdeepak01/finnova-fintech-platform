import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { Account, Api, LedgerEntry } from './services/api';

@Component({
  selector: 'app-root',
  imports: [FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private api = inject(Api);

  // Server state (signals, so the UI updates automatically)
  accounts = signal<Account[]>([]);
  entries = signal<LedgerEntry[]>([]);
  selectedId = signal<number | null>(null);
  loading = signal(true);
  error = signal('');
  notice = signal('');

  selected = computed(() => this.accounts().find(a => a.id === this.selectedId()) ?? null);
  otherAccounts = computed(() => this.accounts().filter(a => a.id !== this.selectedId()));

  // Form state
  newOwner = signal('');
  newDeposit = signal<number | null>(0);
  amount = signal<number | null>(null);
  transferTo = signal<number | null>(null);
  transferAmount = signal<number | null>(null);

  ngOnInit(): void {
    this.refresh();
  }

  select(id: number): void {
    this.selectedId.set(id);
    this.transferTo.set(null);
    this.clearMessages();
    this.loadEntries();
  }

  createAccount(): void {
    const name = this.newOwner().trim();
    if (!name) {
      this.error.set('Please enter the account holder name.');
      return;
    }
    this.run(this.api.createAccount(name, this.newDeposit() ?? 0), 'Account created.', acc => {
      this.newOwner.set('');
      this.newDeposit.set(0);
      this.selectedId.set(acc.id);
    });
  }

  deposit(): void {
    const id = this.selectedId();
    const amt = this.amount();
    if (id === null || !amt) { this.error.set('Enter an amount.'); return; }
    this.run(this.api.deposit(id, amt), 'Deposit successful.', () => this.amount.set(null));
  }

  withdraw(): void {
    const id = this.selectedId();
    const amt = this.amount();
    if (id === null || !amt) { this.error.set('Enter an amount.'); return; }
    this.run(this.api.withdraw(id, amt), 'Withdrawal successful.', () => this.amount.set(null));
  }

  transfer(): void {
    const id = this.selectedId();
    const to = this.transferTo();
    const amt = this.transferAmount();
    if (id === null || to === null || !amt) { this.error.set('Choose a destination and enter an amount.'); return; }
    this.run(this.api.transfer(id, to, amt), 'Transfer successful.', () => this.transferAmount.set(null));
  }

  private labels: Record<string, string> = {
    DEPOSIT: 'Deposit',
    WITHDRAWAL: 'Withdrawal',
    TRANSFER_IN: 'Transfer in',
    TRANSFER_OUT: 'Transfer out'
  };

  typeLabel(type: string): string {
    return this.labels[type] ?? type;
  }

  isCredit(type: string): boolean {
    return type === 'DEPOSIT' || type === 'TRANSFER_IN';
  }

  /** Runs a write call, then reloads accounts + statement and shows a message. */
  private run<T>(call: Observable<T>, successMsg: string, onSuccess?: (result: T) => void): void {
    this.clearMessages();
    call.subscribe({
      next: result => {
        onSuccess?.(result);
        this.notice.set(successMsg);
        this.refresh();
      },
      error: err => this.error.set(err?.error?.message ?? 'Something went wrong. Please try again.')
    });
  }

  private refresh(): void {
    this.api.listAccounts().subscribe({
      next: list => {
        this.accounts.set(list);
        this.loading.set(false);
        if (this.selectedId() === null && list.length > 0) {
          this.selectedId.set(list[0].id);
        }
        this.loadEntries();
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Cannot reach the server. The free Render instance may be waking up — retry in ~30 seconds.');
      }
    });
  }

  private loadEntries(): void {
    const id = this.selectedId();
    if (id === null) { this.entries.set([]); return; }
    this.api.getTransactions(id).subscribe({
      next: list => this.entries.set(list),
      error: () => this.entries.set([])
    });
  }

  private clearMessages(): void {
    this.error.set('');
    this.notice.set('');
  }
}
