package org.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.connector.*;

public class ClientGUI extends JFrame implements KeyListener{
    private BookDBConnector connector;
    private MemberDBConnector memberConnector;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable borrowBookTable;
    private NonEditableModel borrowBookModel;
    private final int screenWidth = 1200;
    private final int screenHeight = 800;

    public ClientGUI(BookDBConnector connector, MemberDBConnector memberConnector) {
        this.connector = connector;
        this.memberConnector = memberConnector;
        initialize();
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
        JButton searchButton = new JButton("도서 검색");
        JButton borrowButton = new JButton("도서 대출");
        JButton returnButton = new JButton("도서 반납");
        JButton checkoutButton = new JButton("대출한 도서 조회");
        JButton requestButton = new JButton("도서 요청");
        JButton bestButton = new JButton("인기 도서");

        mainMenuPanel.add(loginButton);
        mainMenuPanel.add(registerButton);
        /*mainMenuPanel.add(searchButton);
        mainMenuPanel.add(borrowButton);
        mainMenuPanel.add(returnButton);
        mainMenuPanel.add(checkoutButton);
        mainMenuPanel.add(requestButton);
        mainMenuPanel.add(bestButton);*/

        // 로그인 패널
        JPanel loginPanel = createLoginPanel();

        // 회원가입 패널
        JPanel registerPanel = createRegisterPanel();

        // 도서 검색 패널
        /*JPanel searchPanel = createSearchPanel();

        // 도서 대출 패널
        JPanel borrowPanel = createBorrowPanel();

        // 도서 반납 패널
        JPanel returnPanel = createReturnPanel();

        //빌린 책 조회 패널
        JPanel checkoutPanel = createCheckoutPanel();

        //도서 요청 패널
        JPanel requestPanel = createRequestPanel();

        JPanel bestPanel = createBestBookListPanel();*/

        // 카드 패널에 각 패널 추가
        cardPanel.add(mainMenuPanel, "Main Menu");
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(registerPanel, "Register");
        /*cardPanel.add(searchPanel, "Search");
        cardPanel.add(borrowPanel, "Borrow");
        cardPanel.add(returnPanel, "Return");
        cardPanel.add(checkoutPanel, "CheckOut");
        cardPanel.add(requestPanel, "Request");
        cardPanel.add(bestPanel, "Best");*/

        // 버튼 이벤트 리스너
        //로그인 필요 없는 기능
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));
        searchButton.addActionListener(e -> cardLayout.show(cardPanel, "Search"));
        bestButton.addActionListener(e -> cardLayout.show(cardPanel, "Best"));


        //로그인이 필요한 기능
        addLoginButton(borrowButton, "Borrow");
        addLoginButton(returnButton, "Return");
        addCheckoutButton(checkoutButton,"CheckOut");
        addLoginButton(requestButton, "Request");

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

        // 이벤트 리스너 추가 (실제 로직은 connector를 통해 구현)
        loginButton.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(passwordField.getPassword());
            if(id.length() < 5  || id.length() > 15 || pw.length() < 5 || pw.length() > 15){
                JOptionPane.showMessageDialog(this, "Input must be 5 to 15 characters.");
            }
            else {
                String loginResult = memberConnector.loginMemberRequest(id, pw);
                JOptionPane.showMessageDialog(this, loginResult);
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

        // 로그인 타이틀
        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        registerPanel.add(titleLabel, BorderLayout.NORTH);

        // ID와 Password 입력 필드
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

            if(name.length() < 5 || name.length() > 10 || id.length() < 5  || id.length() > 15 || pw.length() < 5 || pw.length() > 15){
                JOptionPane.showMessageDialog(this, "Input must be 5 to 15 characters.");
            }

            else if (id.equals(pw)) {
                JOptionPane.showMessageDialog(this, "ID and password can't be same.");
            }

            else if (!pw.equals(rpw)) {
                JOptionPane.showMessageDialog(this, "Password does not match.");
            }

            else if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "E-mail format is incorrect.");
            }

            else {
                /*String registerResult = memberConnector.joinMemeberRequest(id, pw, name);
                JOptionPane.showMessageDialog(this, registerResult);*/
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
            }
        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return registerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel nameSearchLabel = new JLabel("도서명: ");
        JTextField nameSearchField = new JTextField(15);
        JLabel writerSearchLabel = new JLabel("저자: ");
        JTextField writerSearchField = new JTextField(15);
        JLabel publisherSearchLabel = new JLabel("출판사: ");
        JTextField publisherSearchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        JButton cancelButton = new JButton("취소");

        JTable table = new JTable();

        JScrollPane scrollPane = new JScrollPane(table);

        // 검색 패널 상단에 라벨, 검색 필드, 검색 버튼, 취소 버튼 추가
        JPanel topPanel = new JPanel();
        topPanel.add(nameSearchLabel);
        topPanel.add(nameSearchField);
        topPanel.add(writerSearchLabel);
        topPanel.add(writerSearchField);
        topPanel.add(publisherSearchLabel);
        topPanel.add(publisherSearchField);
        topPanel.add(searchButton);
        topPanel.add(cancelButton);
        searchPanel.add(topPanel, BorderLayout.NORTH);

        // 테이블에 스크롤 패널 추가
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        // 검색 버튼 이벤트 리스너
        searchButton.addActionListener(e -> {
            String book = nameSearchField.getText().trim();
            String writer = writerSearchField.getText().trim();
            String publisher = publisherSearchField.getText().trim();

            Object[][] data1 = new Object[0][];
            try {
                data1 = connector.searchBookRequest(book, writer, publisher);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            setTable(data1, table);
        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return searchPanel;
    }


    private JPanel createBorrowPanel() {
        JPanel borrowPanel = new JPanel(new BorderLayout());
        JLabel borrowLabel = new JLabel("대출할 도서 번호:");
        JTextField borrowField = new JTextField(20);
        JButton borrowButton = new JButton("대출");
        JButton cancelButton = new JButton("취소");

        // 검색 패널 상단에 라벨, 검색 필드, 검색 버튼, 취소 버튼 추가
        JPanel topPanel = new JPanel();
        topPanel.add(borrowLabel);
        topPanel.add(borrowField);
        topPanel.add(borrowButton);
        topPanel.add(cancelButton);
        borrowPanel.add(topPanel, BorderLayout.CENTER);

        borrowButton.addActionListener(e -> {
            try {
                int bookNum = Integer.parseInt(borrowField.getText().trim());
                String result = connector.borrowBookRequest(bookNum);
                JOptionPane.showMessageDialog(borrowPanel, result);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(borrowPanel, "input book num");
                throw new RuntimeException(ex);
            }

        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return borrowPanel;
    }

    private JPanel createReturnPanel() {
        JPanel returnPanel = new JPanel(new BorderLayout());
        JButton cancelButton;
        JLabel returnLabel = new JLabel("반납할 도서 번호:");
        JTextField returnField = new JTextField(20);
        JButton returnButton = new JButton("반납");
        cancelButton = new JButton("취소");

        // 검색 패널 상단에 라벨, 검색 필드, 검색 버튼, 취소 버튼 추가
        JPanel topPanel = new JPanel();
        topPanel.add(returnLabel);
        topPanel.add(returnField);
        topPanel.add(returnButton);
        topPanel.add(cancelButton);
        returnPanel.add(topPanel, BorderLayout.CENTER);

        returnButton.addActionListener(e -> {
            try {
                int bookNum = Integer.parseInt(returnField.getText().trim());
                String result = connector.returnBookRequest(bookNum);
                JOptionPane.showMessageDialog(returnPanel, result);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(returnPanel, "input book num");
                throw new RuntimeException(ex);
            }

        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });
        return returnPanel;
    }

    private JPanel createCheckoutPanel() {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        JButton cancelButton = new JButton("취소");

        /*NonEditableModel model  = new NonEditableModel(new Object[]{"고유번호", "도서명", "저자", "출판사"}, 0);
        Object[][] data = connector.borrowBookListRequest();
        for (Object[] row : data) { model.addRow(row);}*/

        borrowBookTable = setTable(connector.borrowBookListRequest());

        JScrollPane scrollPane = new JScrollPane(borrowBookTable);

        JPanel topPanel = new JPanel();
        topPanel.add(cancelButton);
        checkoutPanel.add(topPanel, BorderLayout.NORTH);

        // 테이블에 스크롤 패널 추가
        checkoutPanel.add(scrollPane, BorderLayout.CENTER);

        // 검색 버튼 이벤트 리스너
        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return checkoutPanel;
    }

    private JPanel createRequestPanel() {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BorderLayout());
        requestPanel.setBorder(BorderFactory
                .createEmptyBorder(screenWidth / 4, screenWidth / 4, screenWidth / 4, screenWidth / 4));

        // 로그인 타이틀
        JLabel titleLabel = new JLabel("Book Request", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        requestPanel.add(titleLabel, BorderLayout.NORTH);

        // ID와 Password 입력 필드
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel bookLabel = new JLabel("Book Name:");
        JTextField bookField = new JTextField();
        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();
        JLabel explainLabel = new JLabel("Explain:");
        JTextField explainField = new JTextField();
        explainField.setSize(300,50);
        fieldsPanel.add(bookLabel);
        fieldsPanel.add(bookField);
        fieldsPanel.add(authorLabel);
        fieldsPanel.add(authorField);
        fieldsPanel.add(explainLabel);
        fieldsPanel.add(explainField);
        requestPanel.add(fieldsPanel, BorderLayout.CENTER);

        // 회원가입 및 취소 버튼
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton requestButton = new JButton("요청");
        JButton cancelButton = new JButton("취소");
        buttonsPanel.add(requestButton);
        buttonsPanel.add(cancelButton);
        requestPanel.add(buttonsPanel, BorderLayout.SOUTH);

        requestButton.addActionListener(e -> {
            String book = bookField.getText();
            String author = authorField.getText();
            String explain = explainField.getText();

            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedTime = today.format(formatter);

            String requestResult = connector.requestBookRequest(formattedTime, book, author, explain);
            JOptionPane.showMessageDialog(this, requestResult);
        });

        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });
        return requestPanel;
    }

    private JPanel createBestBookListPanel() {
        JPanel bestPanel = new JPanel(new BorderLayout());
        JButton cancelButton = new JButton("취소");

        JTable table = setBestTable(connector.getBestBookRequest());

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topPanel = new JPanel();
        topPanel.add(cancelButton);
        bestPanel.add(topPanel, BorderLayout.NORTH);

        // 테이블에 스크롤 패널 추가
        bestPanel.add(scrollPane, BorderLayout.CENTER);

        // 검색 버튼 이벤트 리스너
        cancelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Main Menu"); // 메인 메뉴로 돌아가기
        });

        return bestPanel;
    }

    //로그인 유저만 사용할 수 있는 버튼
    private void addLoginButton(JButton button, String panelName) {
        button.addActionListener(e -> {
            if (BookDBConnector.isLoggedIn()) {
                cardLayout.show(cardPanel, panelName);
            } else {
                JOptionPane.showMessageDialog(this, "Login Please");
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
    }

    private void addCheckoutButton(JButton button, String panelName) {
        button.addActionListener(e -> {
            if (BookDBConnector.isLoggedIn()) {
                borrowBookModel = new NonEditableModel(new Object[]{"고유번호", "도서명", "저자", "출판사"}, 0);
                for (Object[] row : connector.borrowBookListRequest()) { borrowBookModel.addRow(row); }
                borrowBookTable.setModel(borrowBookModel);
                cardLayout.show(cardPanel, panelName);
            } else {
                JOptionPane.showMessageDialog(this, "Login Please");
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
    }

    static class NonEditableModel extends DefaultTableModel {
        public NonEditableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }
        // 모든 셀을 변경 불가능하게 설정
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // 셀을 편집할 수 없도록 false 반환
        }
    }

    public JTable setTable(Object[][] data){
        JTable table = new JTable();
        if (!(data == null)) {
            NonEditableModel model  = new NonEditableModel(new Object[]{"고유번호", "도서명", "저자", "출판사"}, 0);
            for (Object[] row : data) { model.addRow(row);}
            table.setModel(model);
        }
        return table;
    }

    public void setTable(Object[][] data, JTable table) {
        if(!(data == null)) {
            NonEditableModel model = new NonEditableModel(new Object[]{"고유번호", "도서명", "저자", "출판사"}, 0);
            for (Object[] row : data) { model.addRow(row); }
            table.setModel(model);
        }
    }

    public JTable setBestTable(Object[][] data){
        if (data == null) {
            // null인 경우 예외 처리
            return null;
        } else {
            JTable table = new JTable();
            NonEditableModel model  = new NonEditableModel(new Object[]{"순위", "고유번호", "도서명", "저자", "출판사"}, 0);
            for (Object[] row : data) { model.addRow(row);}
            table.setModel(model);
            return table;
        }
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


