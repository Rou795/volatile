package hw.netology;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    static AtomicInteger count3 = new AtomicInteger(0);
    static AtomicInteger count4 = new AtomicInteger(0);
    static AtomicInteger count5 = new AtomicInteger(0);

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }
// поток проверки условий палиндрома и одинаковой буквы.
// Разводить по потокам смысла не было, так как текущая проверка через сортировку
// подхватывает оба эти варианта
        Thread thread1 = new Thread(() -> {
            for (String word : texts) {
                StringBuilder builder = new StringBuilder(word);
                if (builder.reverse().toString().equals(word)) {
                    if (word.length() == 3) {
                        count3.getAndAdd(1);
                    } else if (word.length() == 4) {
                        count4.getAndAdd(1);
                    } else {
                        count5.getAndAdd(1);
                    }
                }
            }
        });

// поток для проверки третьего условия - буквы в алфавитном порядке.

        Thread thread2 = new Thread(() -> {
            for (String word : texts) {
                char[] wordArray = word.toCharArray();
                AtomicInteger count = new AtomicInteger(0);
                for (int i = 1; i < wordArray.length; i++) {
                    if (wordArray[i - 1] <= wordArray[i]) {
                        count.getAndAdd(1);
                    } else {
                        break;
                    }
                }
                if (count.get() == (wordArray.length - 1) && wordArray[0] != wordArray[1]) {
                    if (word.length() == 3) {
                        count3.getAndAdd(1);
                    } else if (word.length() == 4) {
                        count4.getAndAdd(1);
                    } else {
                        count5.getAndAdd(1);
                    }
                }
            }
        });
        threads.add(thread1);
        threads.add(thread2);
        thread1.start();
        thread2.start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("Красивых слов с длиной 3: " + count3 + " шт.");
        System.out.println("Красивых слов с длиной 4: " + count4 + " шт.");
        System.out.println("Красивых слов с длиной 5: " + count5 + " шт.");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}