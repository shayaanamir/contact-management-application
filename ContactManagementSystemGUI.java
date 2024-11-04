import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class Contact {
    private String name, phoneNumber, email;

    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() { 
        return name; 
    }

    public String getPhoneNumber() { 
        return phoneNumber; 
    }

    public String getEmail() { 
        return email; 
    }

    @Override
    public String toString() {
        return String.format("Name: %s%nPhone: %s%nEmail: %s", name, phoneNumber, email);
    }
}

class PersonalContact extends Contact {
    private String relation;

    public PersonalContact(String name, String phoneNumber, String email, String relation) {
        super(name, phoneNumber, email);
        this.relation = relation;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%nRelation: %s%n", relation);
    }
}

class BusinessContact extends Contact {
    private String companyName, jobTitle;

    public BusinessContact(String name, String phoneNumber, String email, String companyName, String jobTitle) {
        super(name, phoneNumber, email);
        this.companyName = companyName;
        this.jobTitle = jobTitle;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%nCompany: %s%nJob Title: %s%n", companyName, jobTitle);
    }
}

class ContactsList {
    private final List<Contact> personalContacts = new ArrayList<>();
    private final List<Contact> businessContacts = new ArrayList<>();

    public void addPersonalContact(PersonalContact contact){ 
        personalContacts.add(contact); 
    }

    public void addBusinessContact(BusinessContact contact){ 
        businessContacts.add(contact); 
    }

    public void removePersonalContact(int index){ 
        personalContacts.remove(index); 
    }

    public void removeBusinessContact(int index){
        businessContacts.remove(index); 
    }

    public List<Contact> getPersonalContacts(){ 
        return personalContacts; 
    }

    public List<Contact> getBusinessContacts() { 
        return businessContacts; 
    }

    public Contact searchByName(String name) {
        for (Contact contact : personalContacts) {
            if (contact.getName().equalsIgnoreCase(name)) 
                return contact;
        }

        for (Contact contact : businessContacts) {
            if (contact.getName().equalsIgnoreCase(name)) 
                return contact;
        }

        return null;
    }
}

public class ContactManagementSystemGUI {
    private JFrame frame;
    private ContactsList contactsList = new ContactsList();

    private JTextField[] personalFields, businessFields;
    private JList<String> personalContactList, businessContactList;
    private DefaultListModel<String> personalListModel, businessListModel;
    private JTextArea contactDetailsArea;

    public ContactManagementSystemGUI() {
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Contact Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1000);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createInputPanel(), BorderLayout.NORTH);
        mainPanel.add(createContactListPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        contactDetailsArea = new JTextArea(5, 40);
        contactDetailsArea.setEditable(false);
        contactDetailsArea.setBorder(BorderFactory.createTitledBorder("Contact Details"));
        mainPanel.add(new JScrollPane(contactDetailsArea), BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setVisible(true);

    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        personalFields = createContactFields("Add Personal Contact", inputPanel);
        businessFields = createContactFields("Add Business Contact", inputPanel);
        
        return inputPanel;
    }

    private JTextField[] createContactFields(String title, JPanel inputPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = title.equals("Add Personal Contact") ?
            new String[]{"Name:", "Phone:", "Email:", "Relation:"} :
            new String[]{"Name:", "Phone:", "Email:", "Company:", "Job Title:"};

        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fields[i] = new JTextField(15);
            panel.add(fields[i], gbc);
        }

        JButton addButton = new JButton(title.equals("Add Personal Contact") ? "Add Personal Contact" : "Add Business Contact");
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);
        
        addButton.addActionListener(e -> {
            try {
                if (title.equals("Add Personal Contact")) addPersonalContact(fields);
                else addBusinessContact(fields);
            } catch (IllegalArgumentException ex) {
                showError("Input Error", ex.getMessage());
            }
        });

        inputPanel.add(panel);
        return fields;
    }

    private JPanel createContactListPanel() {
        JPanel displayPanel = new JPanel(new GridLayout(1, 2));
    
        personalListModel = new DefaultListModel<>();
        personalContactList = new JList<>(personalListModel);
        personalContactList.setBorder(BorderFactory.createTitledBorder("Personal Contacts"));
        businessListModel = new DefaultListModel<>();
        businessContactList = new JList<>(businessListModel);
        businessContactList.setBorder(BorderFactory.createTitledBorder("Business Contacts"));
    

        personalContactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (personalContactList.getSelectedIndex() != -1) {
                    businessContactList.clearSelection(); 
                    displayContactDetails(personalContactList.getSelectedValue());
                }
            }
        });
    
        businessContactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (businessContactList.getSelectedIndex() != -1) {
                    personalContactList.clearSelection(); 
                    displayContactDetails(businessContactList.getSelectedValue());  
                }
            }
        });
    
        displayPanel.add(new JScrollPane(personalContactList));
        displayPanel.add(new JScrollPane(businessContactList));
    
        return displayPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
    
        JButton removeContactButton = new JButton("Remove Contact");
        JButton searchButton = new JButton("Search Contact");
    
        removeContactButton.addActionListener(e -> removeContact());
    
        searchButton.addActionListener(this::searchContact);
    
        buttonPanel.add(removeContactButton);
        buttonPanel.add(searchButton);
    
        return buttonPanel;
    }

    private void addPersonalContact(JTextField[] fields) {
        if (areFieldsEmpty(fields)) throw new IllegalArgumentException("All fields must be filled.");
        
        PersonalContact contact = new PersonalContact(fields[0].getText().trim(), fields[1].getText().trim(),
                fields[2].getText().trim(), fields[3].getText().trim());
        contactsList.addPersonalContact(contact);
        personalListModel.addElement(contact.getName());
        clearFields(fields);
    }

    private void addBusinessContact(JTextField[] fields) {
        if (areFieldsEmpty(fields)) throw new IllegalArgumentException("All fields must be filled.");
        
        BusinessContact contact = new BusinessContact(fields[0].getText().trim(), fields[1].getText().trim(),
                fields[2].getText().trim(), fields[3].getText().trim(), fields[4].getText().trim());
        contactsList.addBusinessContact(contact);
        businessListModel.addElement(contact.getName());
        clearFields(fields);
    }

    private boolean areFieldsEmpty(JTextField[] fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void removeContact() {
        int personalSelectedIndex = personalContactList.getSelectedIndex();
        int businessSelectedIndex = businessContactList.getSelectedIndex();
    
        if (personalSelectedIndex != -1) {
            contactsList.removePersonalContact(personalSelectedIndex);
            personalListModel.remove(personalSelectedIndex);
            contactDetailsArea.setText("");  
        } 
        else if (businessSelectedIndex != -1) {     
            contactsList.removeBusinessContact(businessSelectedIndex);
            businessListModel.remove(businessSelectedIndex);
            contactDetailsArea.setText(""); 
        } 
        else {
            showError("Error", "Please select a contact to remove.");
        }
    }
    

    private void searchContact(ActionEvent e) {
        String nameToSearch = JOptionPane.showInputDialog(frame, "Enter the name to search:");
        if (nameToSearch != null && !nameToSearch.trim().isEmpty()) {
            Contact foundContact = contactsList.searchByName(nameToSearch.trim());
            contactDetailsArea.setText(foundContact != null ? foundContact.toString() : "Contact not found.");
        } else {
            showError("Error", "Please enter a name to search.");
        }
    }

    private void displayContactDetails(String selectedValue) {
        if (selectedValue != null) {
            for (Contact contact : contactsList.getPersonalContacts()) {
                if (contact.getName().equals(selectedValue)) {
                    contactDetailsArea.setText(contact.toString());
                    return;
                }
            }
            for (Contact contact : contactsList.getBusinessContacts()) {
                if (contact.getName().equals(selectedValue)) {
                    contactDetailsArea.setText(contact.toString());
                    return;
                }
            }
        }
    }

    private void clearFields(JTextField[] fields) {
        for (JTextField field : fields) field.setText("");
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ContactManagementSystemGUI::new);
    }
}