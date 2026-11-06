import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Account {
  id: number;
  ownerName: string;
  accountNumber: string;
  balance: number;
  createdAt: string;
}

export type EntryType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER_IN' | 'TRANSFER_OUT';

export interface LedgerEntry {
  id: number;
  type: EntryType;
  amount: number;
  balanceAfter: number;
  description: string | null;
  counterpartAccountNumber: string | null;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class Api {
  private http = inject(HttpClient);
  private base = environment.apiUrl;
  private accounts = `${this.base}/api/accounts`;

  getMessage(): Observable<string> {
    return this.http.get(`${this.base}/hello`, { responseType: 'text' });
  }

  listAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.accounts);
  }

  createAccount(ownerName: string, initialDeposit: number): Observable<Account> {
    return this.http.post<Account>(this.accounts, { ownerName, initialDeposit });
  }

  getTransactions(id: number): Observable<LedgerEntry[]> {
    return this.http.get<LedgerEntry[]>(`${this.accounts}/${id}/transactions`);
  }

  deposit(id: number, amount: number): Observable<Account> {
    return this.http.post<Account>(`${this.accounts}/${id}/deposit`, { amount });
  }

  withdraw(id: number, amount: number): Observable<Account> {
    return this.http.post<Account>(`${this.accounts}/${id}/withdraw`, { amount });
  }

  transfer(id: number, toAccountId: number, amount: number): Observable<Account> {
    return this.http.post<Account>(`${this.accounts}/${id}/transfer`, { toAccountId, amount });
  }
}
