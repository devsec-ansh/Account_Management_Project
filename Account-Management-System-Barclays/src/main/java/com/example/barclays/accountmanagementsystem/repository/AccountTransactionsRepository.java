package com.example.barclays.accountmanagementsystem.repository;

import com.example.barclays.accountmanagementsystem.entity.AccountTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionsRepository extends JpaRepository<AccountTransactions,Integer> {
}
