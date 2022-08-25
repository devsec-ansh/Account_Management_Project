package com.example.barclays.accountmanagementsystem.repository;

import com.example.barclays.accountmanagementsystem.entity.CustomerBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerBankAccountRepository extends JpaRepository<CustomerBankAccount, Integer> {
}
