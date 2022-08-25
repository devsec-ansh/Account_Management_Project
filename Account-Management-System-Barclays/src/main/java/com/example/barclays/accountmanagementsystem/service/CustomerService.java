package com.example.barclays.accountmanagementsystem.service;
import com.example.barclays.accountmanagementsystem.dto.TransactionDetails;
import com.example.barclays.accountmanagementsystem.entity.AccountTransactions;
import com.example.barclays.accountmanagementsystem.entity.Customer;
import com.example.barclays.accountmanagementsystem.entity.CustomerBankAccount;
import com.example.barclays.accountmanagementsystem.repository.AccountTransactionsRepository;
import com.example.barclays.accountmanagementsystem.repository.CustomerBankAccountRepository;
import com.example.barclays.accountmanagementsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountTransactionsRepository accountTransactionsRepository;

    @Autowired
    private CustomerBankAccountRepository bankAccountRepository;

    public Customer customerDetails(int customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    public List<AccountTransactions> transactions(int accountNo, Integer customerId) {

        Customer customer=customerDetails(customerId);
        List<CustomerBankAccount> cref = customer.getAccounts();

        for (int i = 0; i < cref.size(); i++) {
            if (cref.get(i).getAccountNo() == accountNo) {
                System.out.println("Account Found");
                List<AccountTransactions> transactionDetails = cref.get(i).getAccountTransactions();
                for (AccountTransactions element : transactionDetails) {
                    System.out.println("Type= " + element.getType());
                }
                return transactionDetails;
//                      return null;
            }
        }


        return null;
    }

    public boolean cashWithDrawl(Integer accountNo, AccountTransactions accountTransactions)
    {
        double tId=Math.random();
        tId=Math.floor(tId*1000);
        int transactionReferenceNo=(int)tId*100;
        Date date=new Date();
        accountTransactions.setDate(date);
        accountTransactions.setTransactionReferenceNo(transactionReferenceNo);
        //actual acount deduct money
        CustomerBankAccount account = bankAccountRepository.getById(accountNo);
        double balanceAmount=account.getBalanceAmount()-accountTransactions.getAmount();
        account.setBalanceAmount(balanceAmount);
        List<AccountTransactions> transactions = account.getAccountTransactions();
        transactions.add(accountTransactionsRepository.save(accountTransactions));
        account.setAccountTransactions(transactions);
        bankAccountRepository.save(account);
        return true;
    }

    public boolean cashDeposit(Integer accountNo,AccountTransactions accountTransactions)
    {
        double tId=Math.random();
        tId=Math.floor(tId*1000);
        int transactionReferenceNo=(int)tId*100;
        Date date=new Date();
        accountTransactions.setDate(date);
        accountTransactions.setTransactionReferenceNo(transactionReferenceNo);
        CustomerBankAccount account = bankAccountRepository.getById(accountNo);
        double balanceAmount=account.getBalanceAmount()+accountTransactions.getAmount();
        account.setBalanceAmount(balanceAmount);
        List<AccountTransactions> transactions = account.getAccountTransactions();
        transactions.add(accountTransactionsRepository.save(accountTransactions));
        account.setAccountTransactions(transactions);
        bankAccountRepository.save(account);
        return true;
    }

    public boolean transferAmount(Integer accountNo,AccountTransactions accountTransactions){
        double tId1=Math.random();
        tId1=Math.floor(tId1*1000);
        int transactionReferenceNo=(int)tId1*100;
        accountTransactions.setTransactionReferenceNo(transactionReferenceNo);

        AccountTransactions accountTransactions1=new AccountTransactions();
        double tId2=Math.random();
        tId2=Math.floor(tId2*1000);
        int toAccountNumber=accountTransactions.getToAccount();
        int transactionReferenceNo2=(int)tId2*100;
        accountTransactions1.setTransactionReferenceNo(transactionReferenceNo2);
        accountTransactions1.setSubType(accountTransactions.getType());
        accountTransactions1.setDate(accountTransactions.getDate());
        accountTransactions1.setToAccount(accountNo);
        accountTransactions1.setAmount(accountTransactions.getAmount());
        String type=accountTransactions.getType();
        if(type=="Credit")
        {
            accountTransactions.setType("Debit");
            accountTransactions1.setType("Credit");
        }
       else {
            accountTransactions.setType("Credit");
            accountTransactions1.setType("Debit");
        }

       CustomerBankAccount fromAccount=bankAccountRepository.getById(accountNo);
       List<AccountTransactions> transactionsRef=fromAccount.getAccountTransactions();
       transactionsRef.add(accountTransactionsRepository.save(accountTransactions));
        fromAccount.setAccountTransactions(transactionsRef);
        fromAccount.setBalanceAmount(fromAccount.getBalanceAmount()-accountTransactions.getAmount());
        bankAccountRepository.save(fromAccount);

       CustomerBankAccount toAccount=bankAccountRepository.getById(toAccountNumber);
        List<AccountTransactions> transactionsRef1=toAccount.getAccountTransactions();
        transactionsRef1.add(accountTransactionsRepository.save(accountTransactions1));
        toAccount.setAccountTransactions(transactionsRef1);
        toAccount.setBalanceAmount(toAccount.getBalanceAmount()+accountTransactions.getAmount());
        bankAccountRepository.save(toAccount);
        return true;
    }

    public int activeAccounts(int customerId)
    {
        Customer customer=customerDetails(customerId);
        return customer.getAccounts().size();
    }

    public int latestDebitAmount(int accountNo,int customerId)
    {
             Customer customer=customerDetails(customerId);
             CustomerBankAccount customerBankAccount=bankAccountRepository.getById(accountNo);
             if(customerBankAccount!=null) {
                 List<AccountTransactions> accountTransactionsList = customerBankAccount.getAccountTransactions();
                 Collections.reverse(accountTransactionsList);
                 for (AccountTransactions accountTransactions : accountTransactionsList) {
                     if (accountTransactions.getType().equals("Debit")) {
                         return (int) accountTransactions.getAmount();
                     }
                 }
             }


        return 0;
    }

    public int latestCreditAmount(int accountNo,int customerId)
    {
        Customer customer=customerDetails(customerId);
        CustomerBankAccount customerBankAccount=bankAccountRepository.getById(accountNo);

        List<AccountTransactions> accountTransactionsList=customerBankAccount.getAccountTransactions();
            Collections.reverse(accountTransactionsList);
            for(AccountTransactions accountTransactions:accountTransactionsList)
            {
                if(accountTransactions.getType().equals("Credit"))
                {
                    return (int)accountTransactions.getAmount();
                }
            }


        return 0;
    }

    public double balanceAmount (int accountNo)
    {
        CustomerBankAccount customerBankAccount=bankAccountRepository.getById(accountNo);
        return customerBankAccount.getBalanceAmount();
    }

    public List<Integer> getAccountNumbers(int customerId){
        List<Integer> accounts = new ArrayList<>();
        try {Customer customer = customerRepository.findById(customerId).get();
            for (CustomerBankAccount acc : customer.getAccounts()) {accounts.add(acc.getAccountNo());}
        }catch (Exception e){}
        return accounts;
    }

    public List<Integer> getAccountNumbers(String pancardid){
        List<Integer> accounts = new ArrayList<>();
        try {Customer customer = customerRepository.findByPanCardNo(pancardid).get();
            for (CustomerBankAccount acc : customer.getAccounts()) {accounts.add(acc.getAccountNo());}
        }catch (Exception e){}
        return accounts;
    }
}
