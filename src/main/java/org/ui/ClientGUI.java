package org.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.request.*;

public class ClientGUI extends JFrame implements KeyListener{
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private EmailRequest er;
    private final static int screenWidth = 1200;
    private final static int screenHeight = 800;

    public ClientGUI() {
        initialize();
        er = new EmailRequest();
    }

    private void initialize() {
        setTitle("로그인 시스템");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 메인 메뉴 패널
        JPanel mainMenuPanel = new JPanel();
        JButton loginButton = new JButton("로그인");
        JButton registerButton = new JButton("회원가입");
        JButton setEmailButton = new JButton("이메일 설정");
        JButton getEmailButton = new JButton("이메일 확인");

        mainMenuPanel.add(loginButton);
        mainMenuPanel.add(registerButton);
        mainMenuPanel.add(setEmailButton);
        mainMenuPanel.add(getEmailButton);

        // 로그인 패널
        JPanel loginPanel = createLoginPanel();

        // 회원가입 패널
        JPanel registerPanel = createRegisterPanel();

        // 카드 패널에 각 패널 추가
        cardPanel.add(mainMenuPanel, "Main Menu");
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(registerPanel, "Register");

        // 버튼 이벤트 리스너
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));
        setEmailButton.addActionListener(e -> {
            try {
                setEmailPane();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        getEmailButton.addActionListener(e -> {
            try {
                getEmailPane();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(cardPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBorder(BorderFactory
                .createEmptyBorder(screenWidth / 4, screenWidth / 4, screenWidth / 4, screenWidth / 4));

        // 로그인 타이틀
        JLabel titleLabel = new JLabel("Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        // ID와 Password 입력 필드
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        fieldsPanel.add(idLabel);
        fieldsPanel.add(idField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);
        loginPanel.add(fieldsPanel, BorderLayout.CENTER);

        // 로그인 및 취소 버튼
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("로그인");
        JButton cancelButton = new JButton("취소");
        buttonsPanel.add(loginButton);
        buttonsPanel.add(cancelButton);
        loginPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // 이벤트 리스너 추가
        loginButton.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(passwordField.getPassword());

            if (id.length() < 5 || id.length() > 15 || pw.length() < 5 || pw.length() > 15) {
                JOptionPane.showMessageDialog(this, "Input must be 5 to 15 characters.");
                //특수문자 체크
            } else if (containsSpecialChars(id) || containsSpecialChars(pw)) {
                JOptionPane.showMessageDialog(this, "Input contains invalid characters.");
            } else {

                LoginRequest request = new LoginRequest();
                String result = null;
                try {
                    result = request.login(id,pw);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, result);
            }
        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return loginPanel;
    }


    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BorderLayout());
        registerPanel.setBorder(BorderFactory
                .createEmptyBorder(screenHeight / 3, screenWidth / 4, screenHeight / 3, screenWidth / 4));

        // 회원가입 타이틀
        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        registerPanel.add(titleLabel, BorderLayout.NORTH);

        // 입력 필드
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);
        JLabel emailLabel = new JLabel("E-Mail:");
        JTextField emailField = new JTextField();
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JLabel recPasswordLabel = new JLabel("Reconfirm Password:");
        JPasswordField recPasswordField = new JPasswordField(15);
        JLabel pwStrengthLabel = new JLabel();
        updateStrengthLabel(0, pwStrengthLabel);

        fieldsPanel.add(idLabel);
        fieldsPanel.add(idField);
        fieldsPanel.add(emailLabel);
        fieldsPanel.add(emailField);
        fieldsPanel.add(nameLabel);
        fieldsPanel.add(nameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(recPasswordLabel);
        fieldsPanel.add(recPasswordField);
        registerPanel.add(fieldsPanel, BorderLayout.CENTER);

        // 회원가입 및 취소 버튼
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("회원가입");
        JButton cancelButton = new JButton("취소");
        buttonsPanel.add(pwStrengthLabel);
        buttonsPanel.add(registerButton);
        buttonsPanel.add(cancelButton);
        registerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String id = idField.getText();
            String pw = new String(passwordField.getPassword());
            String rpw = new String(recPasswordField.getPassword());
            updateStrengthLabel(checkPasswordStrength(pw), pwStrengthLabel);

            if (name.length() < 5 || name.length() > 10 || id.length() < 5 || id.length() > 15 || pw.length() < 5 || pw.length() > 15) {
                JOptionPane.showMessageDialog(this, "Input must be 5 to 15 characters.");
            } else if (containsSpecialChars(name) || containsSpecialChars(email) || containsSpecialChars(id) || containsSpecialChars(pw)) {
                JOptionPane.showMessageDialog(this, "Input contains invalid characters.");
            } else if (id.equals(pw)) {
                JOptionPane.showMessageDialog(this, "ID and password can't be same.");
            } else if (!pw.equals(rpw)) {
                JOptionPane.showMessageDialog(this, "Password does not match.");
            } else if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "E-mail format is incorrect.");
            } else {

                //여기다 회원가입 관련 요청 매핑
                RegisterRequest request = new RegisterRequest();
                String result = null;
                try {
                    result = request.register(id,pw,email);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, result);
            }
        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return registerPanel;
    }

    private void setEmailPane() throws IOException {
        String email = JOptionPane.showInputDialog(this, "Reset Your Email:");
        if (email != null) { // If user presses OK
            if (email.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(this, "Input Something");
            } else if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "E-mail format is incorrect.");
            } else {
                //EmailRequest request = new EmailRequest();
                String result = er.setEmail(email);
                JOptionPane.showMessageDialog(this, result);
            }
        }
    }

    private void getEmailPane() throws IOException {
        //EmailRequest request = new EmailRequest();
        String email = er.getEmail();
        JOptionPane.showMessageDialog(this, email);
    }

    // 특수 문자 검사 함수
    private boolean containsSpecialChars(String input) {
        String specialChars = "['\";=]";
        return input.matches(".*" + specialChars + ".*");
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        String password = ((JTextField) e.getSource()).getText();
        int strength = checkPasswordStrength(password);
        //updateStrengthLabel(strength);
    }

    //비밀번호 강도 표시
    public void updateStrengthLabel(int strength, JLabel strengthLabel) {
        String strengthStr;
        Color color = switch (strength) {
            case 0, 1 -> {
                strengthStr = "Weak";
                yield Color.RED;
            }
            case 2, 3 -> {
                strengthStr = "Moderate";
                yield Color.ORANGE;
            }
            case 4, 5 -> {
                strengthStr = "Strong";
                yield Color.GREEN;
            }
            default -> {
                strengthStr = "Invalid";
                yield Color.BLACK;
            }
        };

        strengthLabel.setText("Password Strength: " + strengthStr);
        strengthLabel.setForeground(color);
    }

    //비밀번호 강도 체크
    public static int checkPasswordStrength(String password) {
        int strength = 0;

        // 비밀번호 길이 체크
        if (password.length() >= 8) {
            strength++;
        }

        // 대문자 포함 여부 체크
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            strength++;
        }

        // 소문자 포함 여부 체크
        if (Pattern.compile("[a-z]").matcher(password).find()) {
            strength++;
        }

        // 숫자 포함 여부 체크
        if (Pattern.compile("[0-9]").matcher(password).find()) {
            strength++;
        }

        // 특수 문자 포함 여부 체크
        if (Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) {
            strength++;
        }

        // 같은 문자열이 반복되는지 체크
        if (hasRepeatingSubstring(password)) {
            strength--;
        }

        return strength;
    }

    //문자열 반복 체크
    private static boolean hasRepeatingSubstring(String str) {
        for (int i = 0; i < str.length() - 1; i++) {
            String substr = str.substring(i, i + 2);
            String restOfString = str.substring(i + 2);
            if (restOfString.contains(substr)) {
                return true;
            }
        }
        return false;
    }
}


