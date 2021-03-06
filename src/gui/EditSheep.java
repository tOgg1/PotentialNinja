/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import db.DatabaseHandler;
import main.Register;
import util.FlagData;
import util.GeneralUtil;

import java.sql.SQLException;
import java.util.Date;

public class EditSheep extends javax.swing.JFrame {

    /**
     * Creates new form EditSheep
     */
	
	private DatabaseHandler mHandler;
	private Register mRegister;
	private String sheepName;
    private int sheepID;
    private int healthflag1;
    private int farmerID;
    private MainMenu main;

    public EditSheep(MainMenu previous, int farmerID, String sheepName, DatabaseHandler mHandler, Register mRegister){
        this.main = previous;
        this.mHandler = mHandler;
        this.mRegister = mRegister;
        this.sheepName = sheepName;
        this.farmerID = farmerID;

        try {
            this.sheepID = mHandler.getSheepByName(sheepName, mRegister.getFarmerID()).getId();
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }
        initComponents();

        this.main.setVisible(false);
        this.main.setFocusable(false);
        this.setLocationRelativeTo(this.main);
    }

    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        label4 = new java.awt.Label();
        label1 = new java.awt.Label();
        jButton1 = new javax.swing.JButton();
        checkbox2 = new java.awt.Checkbox();
        checkbox12 = new java.awt.Checkbox();
        jButton2 = new javax.swing.JButton();
        checkbox10 = new java.awt.Checkbox();
        checkbox5 = new java.awt.Checkbox();
        checkbox1 = new java.awt.Checkbox();
        checkbox4 = new java.awt.Checkbox();
        checkbox15 = new java.awt.Checkbox();
        checkbox14 = new java.awt.Checkbox();
        checkbox9 = new java.awt.Checkbox();
        label5 = new java.awt.Label();
        textField2 = new java.awt.TextField();
        textField3 = new java.awt.TextField();
        checkbox3 = new java.awt.Checkbox();
        checkbox7 = new java.awt.Checkbox();
        checkbox6 = new java.awt.Checkbox();
        checkbox8 = new java.awt.Checkbox();
        checkbox11 = new java.awt.Checkbox();
        label3 = new java.awt.Label();
        label2 = new java.awt.Label();
        label6 = new java.awt.Label();
        textField1 = new java.awt.TextField();
        checkbox13 = new java.awt.Checkbox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        label4.setFont(new java.awt.Font("Dialog", 1, 14));
        label4.setText("Rediger sau");

        label1.setText("ID på sauen");

        textField1.setText(sheepName);

        jButton1.setText("Lagre");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setBackground(new java.awt.Color(240, 240, 240));
        jTextField1.setBorder(null);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Søye");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Vær");

        checkbox2.setLabel("Leverbetennelse");

        checkbox12.setLabel("Skrapesyke");

        jButton2.setText("Tilbake");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        checkbox10.setLabel("Munnskurv");

        checkbox5.setLabel("Klostridieinfeksjoner");

        checkbox1.setLabel("Hjernebarksår");

        checkbox4.setLabel("Annet");

        checkbox15.setLabel("Drektighetsforgiftning");

        checkbox14.setLabel("Trommesyke");

        checkbox9.setLabel("Mastitt");

        label5.setText("Har");

        checkbox3.setLabel("Koli-infeksjon");

        checkbox7.setLabel("Blåtunge");

        checkbox6.setLabel("Vaksinert mot klostridieinfeksjoner");

        checkbox8.setLabel("Lungebetennelse");

        checkbox11.setLabel("Sjodogg");

        label3.setText("Fødselsdato");

        try {
            textField3.setText((GeneralUtil.sdf.format(new Date(mHandler.getSheep(sheepID).getBirthdate()))));
        } catch (SQLException e) {
            e.printStackTrace();
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        label6.setText("Kjønn");
        try {
            if (mHandler.getSheep(sheepID).getSex() == "f"){
                jRadioButton1.setSelected(true);
            }
            else {
                jRadioButton2.setSelected(true);
            }
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        label2.setText("Format: mm/dd/åååå");


        textField1.setText(sheepName);

        checkbox13.setLabel("Øyesykdom");

        try {
            int healthflags = mHandler.getSheep(sheepID).getHealthflags();

            checkbox7.setState((healthflags & FlagData.BLATUNGE) > 0);
            checkbox15.setState((healthflags & FlagData.DREKTIGHETSFORGIFTNING) > 0);
            checkbox1.setState((healthflags & FlagData.HJERNEBARKSAR) > 0);
            checkbox5.setState((healthflags & FlagData.KLOSTIDIEINFEKSJONER) > 0);
            checkbox3.setState((healthflags & FlagData.KOLIINFEKSJON) > 0);
            checkbox2.setState((healthflags & FlagData.LEVERBETENNELSE) > 0);
            checkbox8.setState((healthflags & FlagData.LUNGEBETENNELSE) > 0);
            checkbox9.setState((healthflags & FlagData.MASTITT) > 0);
            checkbox10.setState((healthflags & FlagData.MUNNSKURV) > 0);
            checkbox11.setState((healthflags & FlagData.SJODOGG) > 0);
            checkbox12.setState((healthflags & FlagData.SKRAPESYKE) > 0);
            checkbox14.setState((healthflags & FlagData.TROMMESYKE) > 0);
            checkbox13.setState((healthflags & FlagData.OYESYKDOM) > 0);
            checkbox4.setState((healthflags & FlagData.ANNET) > 0);
            checkbox6.setState((healthflags & FlagData.VAKSINE) > 0);

            healthflag1 = healthflags;

        } catch (SQLException e) {
            e.printStackTrace();
        }


        jMenu1.setText("File");

        jMenuItem1.setText("Til hovedmeny");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("MinSide");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText("Logg ut");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(40, 40, 40)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(checkbox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(checkbox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(checkbox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(checkbox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jButton2)
                                                                                        .addComponent(textField1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(checkbox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGap(22, 22, 22)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                        .addComponent(checkbox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                        .addComponent(checkbox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(checkbox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(checkbox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(0, 0, Short.MAX_VALUE)))))))
                                                .addGap(22, 22, 22))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(71, 71, 71)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jRadioButton2)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(textField3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(jRadioButton1)
                                                                                                .addGap(145, 145, 145)))
                                                                                .addGap(25, 25, 25)
                                                                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(186, 186, 186)
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(textField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jRadioButton1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(checkbox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(checkbox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(checkbox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(checkbox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(checkbox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(checkbox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(checkbox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(checkbox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(checkbox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        pack();
    }

    /**
     * Logs out of the program, from the Menu Bar
     */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {
       System.exit(0);
    }

    /**
     * Saves the info from the textFields to the given sheep
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        this.sheepName = textField1.getText();
        String sex = "m";
        if (jRadioButton1.isSelected()){
            sex = "f";
        }
        if (jRadioButton2.isSelected()){
            sex = "m";
        }

        long birthdate = 0;
        try {
            birthdate = GeneralUtil.validateDate(textField3.getText());
        } catch(Throwable e) {
            e.printStackTrace();
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        int healthflag = 0;
        
        healthflag |= checkbox7.getState() == true ? FlagData.BLATUNGE : 0;
        healthflag |= checkbox15.getState() == true ? FlagData.DREKTIGHETSFORGIFTNING : 0;
        healthflag |= checkbox1.getState() == true ? FlagData.HJERNEBARKSAR : 0;
        healthflag |= checkbox5.getState() == true ? FlagData.KLOSTIDIEINFEKSJONER : 0;
        healthflag |= checkbox3.getState() == true ? FlagData.KOLIINFEKSJON : 0;
        healthflag |= checkbox2.getState() == true ? FlagData.LEVERBETENNELSE : 0;
        healthflag |= checkbox8.getState() == true ? FlagData.LUNGEBETENNELSE : 0;
        healthflag |= checkbox9.getState() == true ? FlagData.MASTITT : 0;
        healthflag |= checkbox10.getState() == true ? FlagData.MUNNSKURV : 0;
        healthflag |= checkbox11.getState() == true ? FlagData.SJODOGG : 0;
        healthflag |= checkbox12.getState() == true ? FlagData.SKRAPESYKE : 0;
        healthflag |= checkbox14.getState() == true ? FlagData.TROMMESYKE : 0;
        healthflag |= checkbox13.getState() == true ? FlagData.OYESYKDOM : 0;
        healthflag |= checkbox4.getState() == true ? FlagData.ANNET : 0;
        healthflag |= checkbox6.getState() == true ? FlagData.VAKSINE : 0;

        int healthflagAdd = 0;
        int healthflagRemove = 0;

        if (healthflag1 == healthflag){
            //Ingen endring i healthflag
        }else{
            // noen grunn til at vi gjør dette og ikke setHealthFlag(healthflag)?
            healthflagAdd = healthflag&(healthflag^healthflag1);
            healthflagRemove = ~healthflag&(healthflag^healthflag1);
            try {
                if(healthflagAdd != 0)
                    mHandler.addSheepHealthFlag(sheepID, healthflagAdd);
                if(healthflagRemove != 0)
                    mHandler.removeSheepHealthFlag(sheepID, healthflagRemove);
            } catch (SQLException e) {
                e.printStackTrace();
                Error error = new Error(this, e.getMessage());
                error.setVisible(true);
            }
        }

        /*
        Redendant code, TO BE REMOVED
        if ((healthflag1 & FlagData.BLATUNGE) != (healthflag & FlagData.BLATUNGE)){
            if ((healthflag & FlagData.BLATUNGE) > 0){
                healthflagAdd = FlagData.BLATUNGE;
            }
            else{
                healthfalgRemove = FlagData.BLATUNGE;
            }
        }

        if ((healthflag1 & FlagData.DREKTIGHETSFORGIFTNING) != (healthflag & FlagData.DREKTIGHETSFORGIFTNING)){
            if ((healthflag & FlagData.DREKTIGHETSFORGIFTNING) > 0){
                healthflagAdd = FlagData.DREKTIGHETSFORGIFTNING;
            }
            else{
                healthfalgRemove = FlagData.DREKTIGHETSFORGIFTNING;
            }
        }

        if ((healthflag1 & FlagData.HJERNEBARKSAR) != (healthflag & FlagData.HJERNEBARKSAR)){
            if ((healthflag & FlagData.HJERNEBARKSAR) > 0){
                healthflagAdd = FlagData.HJERNEBARKSAR;
            }
            else{
                healthfalgRemove = FlagData.HJERNEBARKSAR;
            }
        }

        if ((healthflag1 & FlagData.KLOSTIDIEINFEKSJONER) != (healthflag & FlagData.KLOSTIDIEINFEKSJONER)){
            if ((healthflag & FlagData.KLOSTIDIEINFEKSJONER) > 0){
                healthflagAdd = FlagData.KLOSTIDIEINFEKSJONER;
            }
            else{
                healthfalgRemove = FlagData.KLOSTIDIEINFEKSJONER;
            }
        }

        if ((healthflag1 & FlagData.KOLIINFEKSJON) != (healthflag & FlagData.KOLIINFEKSJON)){
            if ((healthflag & FlagData.KOLIINFEKSJON) > 0){
                healthflagAdd = FlagData.KOLIINFEKSJON;
            }
            else{
                healthfalgRemove = FlagData.KOLIINFEKSJON;
            }
        }

        if ((healthflag1 & FlagData.LEVERBETENNELSE) != (healthflag & FlagData.LEVERBETENNELSE)){
            if ((healthflag & FlagData.LEVERBETENNELSE) > 0){
                healthflagAdd = FlagData.LEVERBETENNELSE;
            }
            else{
                healthfalgRemove = FlagData.LEVERBETENNELSE;
            }
        }

        if ((healthflag1 & FlagData.LUNGEBETENNELSE) != (healthflag & FlagData.LUNGEBETENNELSE)){
            if ((healthflag & FlagData.LUNGEBETENNELSE) > 0){
                healthflagAdd = FlagData.LUNGEBETENNELSE;
            }
            else{
                healthfalgRemove = FlagData.LUNGEBETENNELSE;
            }
        }

        if ((healthflag1 & FlagData.MASTITT) != (healthflag & FlagData.MASTITT)){
            if ((healthflag & FlagData.MASTITT) > 0){
                healthflagAdd = FlagData.MASTITT;
            }
            else{
                healthfalgRemove = FlagData.MASTITT;
            }
        }

        if ((healthflag1 & FlagData.MUNNSKURV) != (healthflag & FlagData.MUNNSKURV)){
            if ((healthflag & FlagData.MUNNSKURV) > 0){
                healthflagAdd = FlagData.MUNNSKURV;
            }
            else{
                healthfalgRemove = FlagData.MUNNSKURV;
            }
        }

        if ((healthflag1 & FlagData.SJODOGG) != (healthflag & FlagData.SJODOGG)){
            if ((healthflag & FlagData.SJODOGG) > 0){
                healthflagAdd = FlagData.SJODOGG;
            }
            else{
                healthfalgRemove = FlagData.SJODOGG;
            }
        }

        if ((healthflag1 & FlagData.SKRAPESYKE) != (healthflag & FlagData.SKRAPESYKE)){
            if ((healthflag & FlagData.SKRAPESYKE) > 0){
                healthflagAdd = FlagData.SKRAPESYKE;
            }
            else{
                healthfalgRemove = FlagData.SKRAPESYKE;
            }
        }

        if ((healthflag1 & FlagData.TROMMESYKE) != (healthflag & FlagData.TROMMESYKE)){
            if ((healthflag & FlagData.TROMMESYKE) > 0){
                healthflagAdd = FlagData.TROMMESYKE;
            }
            else{
                healthfalgRemove = FlagData.TROMMESYKE;
            }
        }

        if ((healthflag1 & FlagData.OYESYKDOM) != (healthflag & FlagData.OYESYKDOM)){
            if ((healthflag & FlagData.OYESYKDOM) > 0){
                healthflagAdd = FlagData.OYESYKDOM;
            }
            else{
                healthfalgRemove = FlagData.OYESYKDOM;
            }
        }

        if ((healthflag1 & FlagData.ANNET) != (healthflag & FlagData.ANNET)){
            if ((healthflag & FlagData.ANNET) > 0){
                healthflagAdd = FlagData.ANNET;
            }
            else{
                healthfalgRemove = FlagData.ANNET;
            }
        }

        if ((healthflag1 & FlagData.VAKSINE) != (healthflag & FlagData.VAKSINE)){
            if ((healthflag & FlagData.VAKSINE) > 0){
                healthflagAdd = FlagData.VAKSINE;
            }
            else{
                healthfalgRemove = FlagData.VAKSINE;
            }
        }*/

        try {
            mHandler.setSheepName(sheepID, sheepName);
        } catch (SQLException e) {
            e.printStackTrace();

            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        try {
            mHandler.setSheepBirthdate(sheepID, birthdate);
        } catch (SQLException e) {
            e.printStackTrace();

            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        try {
            mHandler.setSheepSex(sheepID, sex);
        } catch (SQLException e) {
            e.printStackTrace();
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        jLabel2.setText("Informasjonen er nå lagret.");


    }

    /**
     * Go back to MainMenu
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        main.setVisible(true);
        main.setFocusable(true);
        main.setLocationRelativeTo(this);
        main.reloadInfo();
        this.dispose();
    }

    /**
     * Logs out of the program, from the Menu Bar
     */
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {
    	Welcome welcome = new Welcome(this.main.main, this, mHandler);
        welcome.setVisible(true);
        this.dispose();
    }

    /**
     * Go to MyPage, from the Menu Bar
     */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
    	MyPage mypage = new MyPage(this.main, this, farmerID, mHandler, mRegister);
    	mypage.setVisible(true);
    }

    /**
     * Go to ManMenu, from the Menu Bar
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
    	main.setVisible(true);
        main.setFocusable(true);
        main.setLocationRelativeTo(this);
        this.dispose();
    }

    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private java.awt.Checkbox checkbox1;
    private java.awt.Checkbox checkbox10;
    private java.awt.Checkbox checkbox11;
    private java.awt.Checkbox checkbox12;
    private java.awt.Checkbox checkbox13;
    private java.awt.Checkbox checkbox14;
    private java.awt.Checkbox checkbox15;
    private java.awt.Checkbox checkbox2;
    private java.awt.Checkbox checkbox3;
    private java.awt.Checkbox checkbox4;
    private java.awt.Checkbox checkbox5;
    private java.awt.Checkbox checkbox6;
    private java.awt.Checkbox checkbox7;
    private java.awt.Checkbox checkbox8;
    private java.awt.Checkbox checkbox9;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.TextField textField1;
    private java.awt.TextField textField2;
    private java.awt.TextField textField3;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
}
