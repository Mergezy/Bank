public class Account {
    private double balance;

    public Account(double initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Пополнение: " + amount + ", Баланс: " + balance);
        notifyAll(); // Уведомляем другие потоки, что баланс изменился
    }

    public synchronized void withdraw(double amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Ожидание достижения баланса для снятия: " + amount);
            wait(); // Ожидаем, пока не будет достаточно средств на счете
        }
        balance -= amount;
        System.out.println("Снятие: " + amount + ", Баланс: " + balance);
    }

    public double getBalance() {
        return balance;
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account(1000); // Начальный баланс 1000

        Thread depositThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                double amount = Math.random() * 100 + 1; // Случайное пополнение от 1 до 100
                account.deposit(amount);
                try {
                    Thread.sleep(1000); // Подождать 1 секунду перед следующим пополнением
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread withdrawThread = new Thread(() -> {
            try {
                account.withdraw(3000); // Попытка снять 3000
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        depositThread.start();
        withdrawThread.start();

        try {
            depositThread.join();
            withdrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Остаток на балансе: " + account.getBalance());
    }
}
