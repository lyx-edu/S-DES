import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SimpleEncryptor extends JFrame {
    private JTextField keyText;
    private JTextField messageText;
    private JTextField ciphertextText; // 新增的密文输入框
    private JTextArea resultText;
    private JTextArea bruteForceResults;

    // S-Boxes
    private static final int[][][] SBox1 = {{{0, 1}, {0, 0}, {1, 1}, {1, 0}},
            {{1, 1}, {1, 0}, {0, 1}, {0, 0}},
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
            {{1, 1}, {0, 1}, {0, 0}, {1, 0}}};
    private static final int[][][] SBox2 = {{{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            {{1, 0}, {1, 1}, {0, 1}, {0, 0}},
            {{1, 1}, {0, 0}, {0, 1}, {1, 0}},
            {{1, 0}, {0, 1}, {0, 0}, {1, 1}}};

    public SimpleEncryptor() {
        setTitle("S-DES Encryptor/Decryptor");
        setSize(400, 500); // 调整窗口大小
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        JLabel keyLabel = new JLabel("Enter 10-bit Key:");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(keyLabel, gbc);

        keyText = new JTextField();
        keyText.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(keyText, gbc);

        JLabel messageLabel = new JLabel("Enter 8-bit Message:");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(messageLabel, gbc);

        messageText = new JTextField();
        messageText.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(messageText, gbc);

        JLabel ciphertextLabel = new JLabel("Enter Ciphertext:");
        ciphertextLabel.setFont(new Font("Arial", Font.BOLD, 14)); // 密文标签
        gbc.gridx = 0;
        gbc.gridy = 2; // 调整位置
        add(ciphertextLabel, gbc);

        ciphertextText = new JTextField(); // 密文输入框
        ciphertextText.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(ciphertextText, gbc);

        JButton encryptButton = new JButton("加密");
        JButton decryptButton = new JButton("解密");
        JButton bruteForceButton = new JButton("暴力破解");
        JButton asciiEncryptButton = new JButton("ASCII 加密");
        JButton asciiDecryptButton = new JButton("ASCII 解密");
        encryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        decryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        bruteForceButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        asciiEncryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        asciiDecryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(bruteForceButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        add(buttonPanel, gbc);

        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.add(asciiEncryptButton);
        buttonPanel1.add(asciiDecryptButton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across two columns
        add(buttonPanel1, gbc);

        resultText = new JTextArea();
        resultText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultText.setLineWrap(true);
        resultText.setWrapStyleWord(true);
        resultText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        resultText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultText);
        scrollPane.setPreferredSize(new Dimension(350, 100)); // Set scroll pane size
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across two columns
        add(scrollPane, gbc);

        bruteForceResults = new JTextArea();
        bruteForceResults.setFont(new Font("Monospaced", Font.PLAIN, 14));
        bruteForceResults.setLineWrap(true);
        bruteForceResults.setWrapStyleWord(true);
        bruteForceResults.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        bruteForceResults.setEditable(false);
        JScrollPane bruteForceScrollPane = new JScrollPane(bruteForceResults);
        bruteForceScrollPane.setPreferredSize(new Dimension(350, 100)); // Set scroll pane size
        gbc.gridx = 0;
        gbc.gridy = 6; // 调整位置
        gbc.gridwidth = 2; // Span across two columns
        add(bruteForceScrollPane, gbc);

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processMessage();  // Encrypt
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCiphertext();  // Decrypt from ciphertext input
            }
        });

        bruteForceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bruteForceCrack(); // Start brute force cracking
            }
        });

        asciiEncryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asciiEncrypt();
            }
        });

        asciiDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asciiDecrypt();
            }
        });
    }

    private void processMessage() {
        String key = keyText.getText();
        String message = messageText.getText();

        if (key.length() != 10 || message.length() != 8) {
            JOptionPane.showMessageDialog(null, "Key must be 10 bits and message must be 8 bits.");
            return;
        }

        int[] k = new int[11];
        int[] k1 = new int[9];
        int[] k2 = new int[9];
        int[] input = new int[9];

        for (int i = 0; i < 10; i++) {
            k[i + 1] = key.charAt(i) - '0';
        }

        for (int i = 0; i < 8; i++) {
            input[i + 1] = message.charAt(i) - '0';
        }

        createKey(k, k1, k2);

        encode(input, k1, k2);
    }

    private void processCiphertext() {
        String key = keyText.getText();
        String ciphertext = ciphertextText.getText();
    
        if (key.length() != 10 || ciphertext.length() != 8) {
            JOptionPane.showMessageDialog(null, "Key must be 10 bits and ciphertext must be 8 bits.");
            return;
        }
    
        int[] k = new int[11];
        int[] k1 = new int[9];
        int[] k2 = new int[9];
        int[] input = new int[9];
    
        // 将密钥转化为整型数组
        for (int i = 0; i < 10; i++) {
            k[i + 1] = key.charAt(i) - '0';
        }
    
        // 将密文转化为整型数组
        for (int i = 0; i < 8; i++) {
            input[i + 1] = ciphertext.charAt(i) - '0';
        }
    
        // 创建子密钥
        createKey(k, k1, k2);
    
        decode(input, k1, k2);
    }

    private void createKey(int[] k, int[] k1, int[] k2) {
        int[] temp = new int[11];
        int[] l = new int[6], r = new int[6];

        temp[1] = k[3];
        temp[2] = k[5];
        temp[3] = k[2];
        temp[4] = k[7];
        temp[5] = k[4];
        temp[6] = k[10];
        temp[7] = k[1];
        temp[8] = k[9];
        temp[9] = k[8];
        temp[10] = k[6];

        // Left shift and generate K1
        for (int i = 1; i <= 4; i++) {
            l[i] = temp[i + 1];
            r[i] = temp[i + 6];
        }
        l[5] = temp[1];
        r[5] = temp[6];

        for (int i = 1; i <= 5; i++) {
            temp[i] = l[i];
            temp[i + 5] = r[i];
        }

        k1[1] = temp[6];
        k1[2] = temp[3];
        k1[3] = temp[7];
        k1[4] = temp[4];
        k1[5] = temp[8];
        k1[6] = temp[5];
        k1[7] = temp[10];
        k1[8] = temp[9];

        // Left shift again and generate K2
        for (int i = 1; i <= 3; i++) {
            l[i] = temp[i + 2];
            r[i] = temp[i + 7];
        }
        l[4] = temp[1];
        l[5] = temp[6];
        r[4] = temp[2];
        r[5] = temp[7];

        for (int i = 1; i <= 5; i++) {
            temp[i] = l[i];
            temp[i + 5] = r[i];
        }

        k2[1] = temp[6];
        k2[2] = temp[3];
        k2[3] = temp[7];
        k2[4] = temp[4];
        k2[5] = temp[8];
        k2[6] = temp[5];
        k2[7] = temp[10];
        k2[8] = temp[9];
    }

    // Encryption
    private int[] encode(int[] P, int[] k1, int[] k2) {
        int[] temp = new int[9];
        int[] L0 = new int[5], R0 = new int[5], L1 = new int[5], R1 = new int[5], L2 = new int[5], R2 = new int[5];

        temp[1] = P[2];
        temp[2] = P[6];
        temp[3] = P[3];
        temp[4] = P[1];
        temp[5] = P[4];
        temp[6] = P[8];
        temp[7] = P[5];
        temp[8] = P[7];

        for (int i = 1; i <= 4; i++) {
            L0[i] = temp[i];
            R0[i] = temp[i + 4];
            L1[i] = temp[i + 4];
        }

        f(R0, k1);

        for (int i = 1; i <= 4; i++) {
            R2[i] = R1[i] = L0[i] ^ R0[i];
        }

        f(R1, k2);

        for (int i = 1; i <= 4; i++) {
            L2[i] = R1[i] ^ L1[i];
        }

        temp[1] = L2[4];
        temp[2] = L2[1];
        temp[3] = L2[3];
        temp[4] = R2[1];
        temp[5] = R2[3];
        temp[6] = L2[2];
        temp[7] = R2[4];
        temp[8] = R2[2];

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            sb.append(temp[i]);
        }

        resultText.setText(sb.toString());
        return temp;
    }

    // Decryption
    private void decode(int[] C, int[] k1, int[] k2) {
        int[] temp = new int[9];
        int[] L0 = new int[5], R0 = new int[5], L1 = new int[5], R1 = new int[5], L2 = new int[5], R2 = new int[5];

        temp[1] = C[2];
        temp[2] = C[6];
        temp[3] = C[3];
        temp[4] = C[1];
        temp[5] = C[4];
        temp[6] = C[8];
        temp[7] = C[5];
        temp[8] = C[7];

        for (int i = 1; i <= 4; i++) {
            L2[i] = temp[i];
            R1[i] = R2[i] = temp[i + 4];
        }

        f(R2, k2);

        for (int i = 1; i <= 4; i++) {
            R0[i] = L1[i] = L2[i] ^ R2[i];
        }

        f(L1, k1);

        for (int i = 1; i <= 4; i++) {
            L0[i] = R1[i] ^ L1[i];
        }

        temp[1] = L0[4];
        temp[2] = L0[1];
        temp[3] = L0[3];
        temp[4] = R0[1];
        temp[5] = R0[3];
        temp[6] = L0[2];
        temp[7] = R0[4];
        temp[8] = R0[2];

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            sb.append(temp[i]);
        }

        resultText.setText(sb.toString());
    }

    // Function to perform the f function (Feistel function)
    private void f(int[] R, int[] k) {
        int[] ep = new int[9];
        int[] res = new int[5];
        int[] temp = new int[5];

        ep[1] = R[4];
        ep[2] = R[1];
        ep[3] = R[2];
        ep[4] = R[3];
        ep[5] = R[2];
        ep[6] = R[3];
        ep[7] = R[4];
        ep[8] = R[1];

        for (int i = 1; i <= 8; i++) {
            ep[i] ^= k[i];
        }

        res[1] = SBox1[2 * ep[1] + ep[4]][2 * ep[2] + ep[3]][0];
        res[2] = SBox1[2 * ep[1] + ep[4]][2 * ep[2] + ep[3]][1];
        res[3] = SBox2[2 * ep[5] + ep[8]][2 * ep[6] + ep[7]][0];
        res[4] = SBox2[2 * ep[5] + ep[8]][2 * ep[6] + ep[7]][1];

        temp[1] = res[2];
        temp[2] = res[4];
        temp[3] = res[3];
        temp[4] = res[1];

        for (int i = 1; i <= 4; i++) {
            R[i] = temp[i];
        }
    }

    private void bruteForceCrack() {
        long startTime = System.currentTimeMillis();
        StringBuilder results = new StringBuilder("Brute Force Results:\n");
        String plaintext = messageText.getText(); // 获取明文
        String ciphertext = ciphertextText.getText(); // 获取密文
    
        // 验证输入
        if (plaintext.length() != 8 || ciphertext.length() != 8) {
            JOptionPane.showMessageDialog(null, "Plaintext and ciphertext must be 8 bits.");
            return;
        }
    
        // 转换明文和密文为整型数组
        int[] input = new int[9];
        int[] expectedCiphertext = new int[9];
        for (int i = 0; i < 8; i++) {
            input[i + 1] = plaintext.charAt(i) - '0'; // 明文
            expectedCiphertext[i + 1] = ciphertext.charAt(i) - '0'; // 密文
        }
    
        // 暴力破解
        boolean foundKey = false; // 标志是否找到密钥
        for (int i = 0; i < 1024; i++) {
            // 将 i 转换为二进制形式的密钥
            String binaryKey = String.format("%10s", Integer.toBinaryString(i)).replace(' ', '0');
            int[] k = new int[11];
            for (int j = 0; j < 10; j++) {
                k[j + 1] = binaryKey.charAt(j) - '0';
            }
    
            // 使用该密钥进行加密
            int[] k1 = new int[9];
            int[] k2 = new int[9];
            createKey(k, k1, k2);
            int[] ciphertextAttempt = bruteEncode(input, k1, k2); // 使用该密钥加密明文
    
            // 比较生成的密文与已知密文
            if (Arrays.equals(ciphertextAttempt, expectedCiphertext)) {
                results.append("Found Key: ").append(binaryKey).append("\n");
                foundKey = true; // 标记找到密钥
            }
        }
    
        long endTime = System.currentTimeMillis();
        long bruteForceTime = endTime - startTime;
        results.append("Time taken: ").append(bruteForceTime).append(" ms");
    
        // 如果没有找到密钥，输出相关信息
        if (!foundKey) {
            results.append("No valid key found.\n");
        }
    
        bruteForceResults.setText(results.toString());
    }
    
    //为了区别于有输出的encode而为暴力破解专门写的加密算法
    private int[] bruteEncode(int[] P, int[] k1, int[] k2) {
        int[] temp = new int[9];
        int[] L0 = new int[5], R0 = new int[5], L1 = new int[5], R1 = new int[5], L2 = new int[5], R2 = new int[5];

        temp[1] = P[2];
        temp[2] = P[6];
        temp[3] = P[3];
        temp[4] = P[1];
        temp[5] = P[4];
        temp[6] = P[8];
        temp[7] = P[5];
        temp[8] = P[7];

        for (int i = 1; i <= 4; i++) {
            L0[i] = temp[i];
            R0[i] = temp[i + 4];
            L1[i] = temp[i + 4];
        }

        f(R0, k1);

        for (int i = 1; i <= 4; i++) {
            R2[i] = R1[i] = L0[i] ^ R0[i];
        }

        f(R1, k2);

        for (int i = 1; i <= 4; i++) {
            L2[i] = R1[i] ^ L1[i];
        }

        temp[1] = L2[4];
        temp[2] = L2[1];
        temp[3] = L2[3];
        temp[4] = R2[1];
        temp[5] = R2[3];
        temp[6] = L2[2];
        temp[7] = R2[4];
        temp[8] = R2[2];

        return temp;
    }
    

    private void asciiEncrypt() {
        String input = messageText.getText();
        String key = keyText.getText();
        int[] k = new int[11];
        int[] k1 = new int[9];
        int[] k2 = new int[9];
    
        // 读取密钥并转换为整型数组
        for (int i = 0; i < 10; i++) {
            k[i + 1] = key.charAt(i) - '0';
        }
    
        createKey(k, k1, k2);
    
        StringBuilder resultString = new StringBuilder(); // 存储最终结果
    
        for (char c : input.toCharArray()) {
            // 获取字符的8位二进制表示
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            
            // 创建一个整型数组，长度为8（一个字母的二进制长度）
int[] binaryArray = new int[binary.length()];
for (int i = 0; i < binary.length(); i++) {
    binaryArray[i] = binary.charAt(i) - '0';
}

// 输出生成的 binaryArray
System.out.print("Binary representation for character '" + c + "': ");
for (int bit : binaryArray) {
    System.out.print(bit);
}
System.out.println(); // 换行
            
            
            // 调用encode方法进行加密
            int[] encodedBinaryArray = asciiencode(binaryArray, k1, k2);
    
            // 将加密结果转换为字符并添加到结果字符串中
            StringBuilder encodedBinaryString = new StringBuilder();
            for (int bit : encodedBinaryArray) {
                encodedBinaryString.append(bit);
            }
    
            String byteString = encodedBinaryString.substring(0, 8);
            int asciiValue = Integer.parseInt(byteString, 2);
            resultString.append((char) asciiValue);
        }
    
        // 输出结果
        resultText.setText(resultString.toString()); // 假设resultText是显示结果的组件
    }
    
    private int[] asciiencode(int[] P, int[] k1, int[] k2) {
        int[] temp = new int[9];
        int[] L0 = new int[5], R0 = new int[5], L1 = new int[5], R1 = new int[5], L2 = new int[5], R2 = new int[5];
    
        // 进行初始置换
        temp[1] = P[2-1];
        temp[2] = P[6-1];
        temp[3] = P[3-1];
        temp[4] = P[1-1];
        temp[5] = P[4-1];
        temp[6] = P[8-1];
        temp[7] = P[5-1];
        temp[8] = P[7-1];
    
        // 分割
        for (int i = 1; i <= 4; i++) {
            L0[i] = temp[i];
            R0[i] = temp[i + 4];
            L1[i] = temp[i + 4];
        }
    
        // 第一个F函数的调用
        f(R0, k1);
    
        // 计算R1
        for (int i = 1; i <= 4; i++) {
            R2[i] = R1[i] = L0[i] ^ R0[i];
        }
    
        // 第二个F函数的调用
        f(R1, k2);
    
        // 计算L2
        for (int i = 1; i <= 4; i++) {
            L2[i] = R1[i] ^ L1[i];
        }
    
        // 合并结果
        temp[0] = L2[4];
        temp[1] = L2[1];
        temp[2] = L2[3];
        temp[3] = R2[1];
        temp[4] = R2[3];
        temp[5] = L2[2];
        temp[6] = R2[4];
        temp[7] = R2[2];


        /*StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            sb.append(temp[i]);
        }

        resultText.setText(sb.toString());*/
        
        return temp; // 返回加密结果的数组
    }
    
    

    private void asciiDecrypt() {
        String input = ciphertextText.getText(); // 假设输入的加密文本
        String key = keyText.getText();
        int[] k = new int[11];
        int[] k1 = new int[9];
        int[] k2 = new int[9];
    
        // 读取密钥并转换为整型数组
        for (int i = 0; i < 10; i++) {
            k[i + 1] = key.charAt(i) - '0';
        }
    
        createKey(k, k1, k2);
    
        StringBuilder resultString = new StringBuilder(); // 存储最终结果
    
        for (char c : input.toCharArray()) {
            // 获取字符的8位二进制表示
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            
            // 创建一个整型数组，长度为8
            int[] binaryArray = new int[binary.length()];
            for (int i = 0; i < binary.length(); i++) {
                binaryArray[i] = binary.charAt(i) - '0';
            }
    
            // 输出生成的 binaryArray
            System.out.print("Binary representation for character '" + c + "': ");
            for (int bit : binaryArray) {
                System.out.print(bit);
            }
            System.out.println(); // 换行
            
            // 调用decode方法进行解密
            int[] decodedBinaryArray = asciidecode(binaryArray, k1, k2);
    
            // 将解密结果转换为字符并添加到结果字符串中
            StringBuilder decodedBinaryString = new StringBuilder();
            for (int bit : decodedBinaryArray) {
                decodedBinaryString.append(bit);
            }
    
            String byteString = decodedBinaryString.substring(0, 8);
            int asciiValue = Integer.parseInt(byteString, 2);
            resultString.append((char) asciiValue);
        }
    
        // 输出解密结果
        resultText.setText(resultString.toString()); // 假设resultText是显示结果的组件
    }
    
    private int[] asciidecode(int[] P, int[] k1, int[] k2) {
        int[] temp = new int[9];
        int[] L0 = new int[5], R0 = new int[5], L1 = new int[5], R1 = new int[5], L2 = new int[5], R2 = new int[5];
    
        // 进行初始置换
        temp[1] = P[2-1];
        temp[2] = P[6-1];
        temp[3] = P[3-1];
        temp[4] = P[1-1];
        temp[5] = P[4-1];
        temp[6] = P[8-1];
        temp[7] = P[5-1];
        temp[8] = P[7-1];
    
        // 分割
        for (int i = 1; i <= 4; i++) {
            L2[i] = temp[i];
            R1[i] = R2[i] = temp[i + 4];
        }
    
        // 第二个F函数的调用（与加密流程相反，先使用k2）
        f(R2, k2);
    
        // 计算R1
        for (int i = 1; i <= 4; i++) {
            R0[i] = L1[i] = L2[i] ^ R2[i];
        }
    
        // 第一个F函数的调用（使用k1）
        f(L1, k1);
    
        for (int i = 1; i <= 4; i++) {
            L0[i] = L1[i] ^ R1[i];
        }
    
        // 合并结果并进行逆初始置换
        temp[0] = L0[4];
        temp[1] = L0[1];
        temp[2] = L0[3];
        temp[3] = R0[1];
        temp[4] = R0[3];
        temp[5] = L0[2];
        temp[6] = R0[4];
        temp[7] = R0[2];
    
        return temp; // 返回解密结果的数组
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleEncryptor app = new SimpleEncryptor();
            app.setVisible(true);
        });
    }
}
