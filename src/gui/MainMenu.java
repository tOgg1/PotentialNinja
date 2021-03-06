/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import db.DatabaseHandler;
import main.Main;
import main.Register;
import map.MapSheeps;
import map.MapViewer;
import model.Sheep;
import model.SheepMedicalHistory;
import util.FlagData;
import util.GeneralUtil;
import util.Log;
import util.Vec2;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainMenu extends javax.swing.JFrame implements MapViewer.MapViewerListener {

    public static String welcomeMessage = "Welcome to SheepTracker";

    public Main main;

	private DatabaseHandler mHandler;
	private Register mRegister;
	private String sheepName;
    private int sheepID;
    private String bonde;
    private int farmerID;
    private int historyCount;

    private Vec2 defPos;

    private MapSheeps mainMapLogic;
    private MapSheeps sheepMapLogic;
    private MapViewer mainMap;
    private MapViewer sheepMap;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> task;

    public MainMenu(Main main, JFrame previous, int farmerID, DatabaseHandler mHandler, Register mRegister){
        this.main = main;
        this.mHandler = mHandler;
        this.mRegister = mRegister;
        this.mainMap = new MapViewer();
        this.sheepMap = new MapViewer();
        this.sheepMapLogic = new MapSheeps(mRegister, this.sheepMap);
        this.mainMapLogic = new MapSheeps(mRegister, this.mainMap);
        this.mainMap.addListener(this);
        this.farmerID = farmerID;
        this.historyCount = 3;
        try{
            this.bonde =(String) mHandler.getFarmerInformation(farmerID)[0];
            this.bonde = welcomeMessage + ", " + this.bonde.substring(0,this.bonde.indexOf(" ")!=-1?this.bonde.indexOf(" "):this.bonde.length()) + ".";
            this.defPos = mHandler.getFarmerLocation(farmerID);
            this.mainMap.setMapCenter(this.defPos);
            this.sheepMap.setMapCenter(this.defPos);
            this.mainMapLogic.setCurrentSheepPositions();
        }catch (SQLException e){
            e.printStackTrace();
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkForAlarms();
            }
        }, 0, 2, TimeUnit.MINUTES);

        initComponents();
        if(previous != null){
            this.setLocationRelativeTo(previous);
            previous.dispose();
        }
    }

    private void checkForAlarms(){
        try {
            ArrayList<model.Alarm> alarms = mHandler.getNonShownAlarms(this.farmerID);

            if(alarms == null){
                return;
            }

            for(model.Alarm alarm : alarms){
                (new Alarm(this, alarm.getSheepID(), mRegister.getSheepName(alarm.getSheepID()), GeneralUtil.alarmMessages[alarm.getAlarmFlags()-1])).setVisible(true);
                mHandler.setAlarmAsShown(alarm.getAlarmID());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        label1 = new java.awt.Label();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        label2 = new java.awt.Label();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        textField2 = new java.awt.TextField();
        textField3 = new java.awt.TextField();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jButton5 = new javax.swing.JButton();

        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        label1.setName("");
        label1.setText("Velg sau (ID):");
        jTextField1.setText(sheepName);

        label2.setText(bonde);
        label2.setFont(new Font("Serif", Font.PLAIN, 15));


        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Legg til sau");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        mainMap.getMap().setPreferredSize(new Dimension(400,500));
        sheepMap.getMap().setPreferredSize(new Dimension(300,150));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 240, Short.MAX_VALUE).addComponent(mainMap.getMap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 368, Short.MAX_VALUE).addComponent(mainMap.getMap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE).addComponent(sheepMap.getMap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE).addComponent(sheepMap.getMap())
        );

        textField2.setEditable(false);
        
        textField3.setEditable(false);

        label3.setText("Fødselsdato");

        label4.setText("Kjønn");

        label4.setEnabled(false);
        label4.setVisible(false);

        textField2.setEnabled(false);
        textField2.setVisible(false);

        removeInfo();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("Sykdomshistorie");

        jButton3.setText("Rediger");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton3.setEnabled(false);
        jButton3.setVisible(false);

        jButton4.setText("Død");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton4.setEnabled(false);
        jButton4.setVisible(false);

        jMenu1.setText("File");

        jMenuItem1.setText("MinSide");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Logg ut");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jButton5.setText("Vis mer historie");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.setEnabled(false);
        jButton5.setVisible(false);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton1)
                                                .addGap(14, 14, 14))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jButton5)
                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(textField3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                                        .addGap(12, 12, 12)
                                                                                        .addComponent(textField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                                                .addComponent(label1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                        .addComponent(jButton3)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                                                                        .addComponent(jButton4))
                                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)))
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1))
                                .addGap(19, 19, 19)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton4)
                                        .addComponent(jButton3))
                                .addGap(19, 19, 19))
        );

        label3.setEnabled(false);
        label3.setVisible(false);

        textField3.setEnabled(false);
        textField3.setVisible(false);

        jLabel1.setEnabled(false);
        jLabel1.setVisible(false);

        jList1.setEnabled(false);
        jList1.setVisible(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton2)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );



        pack();
    }

    private void info(String sName){

        Log.d("Main", sName + ", "+sName.length());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            sheepID = mHandler.getSheepByName(sName, mRegister.getFarmerID()).getId();
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        try {
            textField3.setText((sdf.format(new Date(mHandler.getSheep(sheepID).getBirthdate()))));
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }


        try {
            mHandler.getSheepMedicalHistory(sheepID);
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        String sex = "";
        try {
            sex = mHandler.getSheep(sheepID).getSex();
        } catch (SQLException e) {
            e.printStackTrace();
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        if(sex.equals(Sheep.SEX_MALE)){
            textField2.setText("Vær");
        }
        else if(sex.equals(Sheep.SEX_FEMALE)){
            textField2.setText("Søye");
        }
        else{
            textField2.setText("Noe rart har skjedd.");
        }

        String datoSyk;

        List<String> stringsArray = new ArrayList<String>();

        try {
            SheepMedicalHistory medhist = mHandler.getSheepMedicalHistory(sheepID);
            if (medhist == null){
                String dato = sdf.format(new Date());
                stringsArray.add(dato + ": Frisk");
                final String[] finalStrings = new String[stringsArray.size()];
                stringsArray.toArray(finalStrings);
                jList1.setModel(new javax.swing.AbstractListModel(){
                    public int getSize() { return finalStrings.length; }
                    public Object getElementAt(int i) { return finalStrings[i]; }
                });
                jScrollPane3.setViewportView(jList1);
                jScrollPane3.setVisible(false);
            }
            else{
                TreeMap<Long, Integer> treeHist = medhist.getHistory();
                Set<Map.Entry<Long, Integer>> entries = treeHist.entrySet();

                for(Map.Entry<Long, Integer> entry : entries){
                    long datoRaw = entry.getKey();
                    int sykdom = entry.getValue();

                    String dato = sdf.format(new Date(datoRaw));

                    if(sykdom == 0){
                        stringsArray.add(dato + ": Frisk");
                    }

                    if ((sykdom  & FlagData.BLATUNGE) > 0) {
                        datoSyk = dato + ": Blåtunge";
                        stringsArray.add(datoSyk);
                    }

                    if ((sykdom  & FlagData.DREKTIGHETSFORGIFTNING) > 0) {
                        datoSyk = dato + ": Drektighetsforgiftning";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.HJERNEBARKSAR) > 0) {
                        datoSyk = dato + ": Hjernebarksår";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.KLOSTIDIEINFEKSJONER) > 0) {
                        datoSyk = dato + ": Klostrideinfeksjoner";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.KOLIINFEKSJON) > 0) {
                        datoSyk = dato + ": Koliinfeksjon";
                        stringsArray.add(datoSyk);
                    }

                    if ((sykdom  & FlagData.LEVERBETENNELSE) > 0) {
                        datoSyk = dato + ": Leverbetennelse";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.LUNGEBETENNELSE) > 0) {
                        datoSyk = dato + ": Lungebetennelse";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.MASTITT) > 0) {
                        datoSyk = dato + ": Mastitt";
                        stringsArray.add(datoSyk);
                    }

                    if ((sykdom  & FlagData.MUNNSKURV) > 0) {
                        datoSyk = dato + ": Munnskurv";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.SJODOGG) > 0) {
                        datoSyk = dato + ": Sjodogg";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.SKRAPESYKE) > 0) {
                        datoSyk = dato + ": Skrapesyke";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.TROMMESYKE) > 0) {
                        datoSyk = dato + ": Trommesyke";
                        stringsArray.add(datoSyk);
                    }

                    if ((sykdom  & FlagData.OYESYKDOM) > 0) {
                        datoSyk = dato + ": Øyesykdom";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.ANNET) > 0) {
                        datoSyk = dato + ": Annet";
                        stringsArray.add(datoSyk);
                    }
                    if ((sykdom  & FlagData.VAKSINE) > 0) {
                        datoSyk = dato + ": Vaksine";
                        stringsArray.add(datoSyk);
                    }
                }

                final String[] finalStrings = new String[stringsArray.size()];
                stringsArray.toArray(finalStrings);
                jList1.setModel(new javax.swing.AbstractListModel() {
                    public int getSize() { return finalStrings.length; }
                    public Object getElementAt(int i) { return finalStrings[i]; }
                });
                jScrollPane3.setViewportView(jList1);
                jScrollPane3.setVisible(false);
            }
        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }
    }

    public void updateSheeps(){
        this.mRegister.reFetchSheeps();
        this.mainMapLogic.refresh();
    }

    private void initFromMap(Sheep sheep){
        jTextField1.setText(sheep.getName());
        initSmallMap(sheep);
    }

    private void initSmallMap(Sheep sheep){
        historyCount = 3;
        sheepMap.setMapCenter(sheep.getPos());
        sheepMap.removeMarkers();
        sheepMapLogic.setHistoricSheepPosition(sheep.getId(), historyCount);
    }

    private void initChosen(){
        jButton3.setEnabled(true);
        jButton3.setVisible(true);

        jButton4.setEnabled(true);
        jButton4.setVisible(true);

        jButton5.setEnabled(true);
        jButton5.setVisible(true);

        label4.setEnabled(true);
        label4.setVisible(true);

        textField2.setEnabled(true);
        textField2.setVisible(true);

        label3.setEnabled(true);
        label3.setVisible(true);

        textField3.setEnabled(true);
        textField3.setVisible(true);

        jLabel1.setEnabled(true);
        jLabel1.setVisible(true);

        jList1.setEnabled(true);
        jList1.setVisible(true);

        jScrollPane3.setVisible(true);
        sheepMap.getMap().setVisible(true);

        pack();
    }

    private void removeInfo(){
        textField2.setText("");
        textField3.setText("");
        jTextField1.setText("");

        jButton5.setEnabled(false);
        jButton5.setVisible(false);

        jButton3.setEnabled(false);
        jButton3.setVisible(false);

        jButton4.setEnabled(false);
        jButton4.setVisible(false);

        label4.setEnabled(false);
        label4.setVisible(false);

        textField2.setEnabled(false);
        textField2.setVisible(false);

        label3.setEnabled(false);
        label3.setVisible(false);

        textField3.setEnabled(false);
        textField3.setVisible(false);

        jLabel1.setEnabled(false);
        jLabel1.setVisible(false);



        jList1.setModel(new AbstractListModel() {
            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public Object getElementAt(int index) {
                return null;
            }
        });
        jList1.setEnabled(false);
        jList1.setVisible(false);

        sheepMap.getMap().setVisible(false);
        sheepMap.removeMarkers();

        jScrollPane3.setVisible(false);
    }

    public void reloadInfo(){
        this.info(this.sheepName);
        initChosen();
    }

    /**
     * Exits the program, from the Menu Bar
     */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    /**
     * Starts new window with AddSheep
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    	AddSheep addSheep = new AddSheep(this, farmerID, mHandler, mRegister);
    	addSheep.setVisible(true);
    	
    }

    /**
     * Starts MainMenu with the ID of the other sheep
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt){
    	sheepName = jTextField1.getText();
        Sheep sheep = mRegister.getSheepByName(sheepName);
        if(sheep == null){
            Error error = new Error(this, "Fant ingen sau med det navnet");
            error.setVisible(true);
        }
        initSmallMap(sheep);
        info(sheepName);
        initChosen();
    }

    /**
     * Starts a new window where the farmer can edit the information about the sheep
     */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt){
    	EditSheep editSheep = new EditSheep(this, farmerID, sheepName, mHandler, mRegister);
    	editSheep.setVisible(true);

    }

    /**
     * Tell the program that the sheep is dead
     */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        int cause = FlagData.DEATHBYEXECUTION;
        try {
            mHandler.killSheep(sheepID, cause);
            Dead dead = new Dead(this, farmerID, sheepID, mHandler, mRegister);
            dead.setVisible(true);

        } catch (SQLException e) {
            Error error = new Error(this, e.getMessage());
            error.setVisible(true);
        }

        removeInfo();
    	
    }

    /**
     * Logs out of the program, from the Menu Bar
     */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
    	Welcome welcome = new Welcome(this.main, this, mHandler);
        welcome.setVisible(true);
        this.dispose();
    }

    /**
     * Show more history
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        historyCount++;
        sheepMapLogic.setHistoricSheepPosition(this.sheepID, historyCount);
    }

    /**
     * Go to MyPage, from the MenuBar
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
    	MyPage mypage = new MyPage(this, this, farmerID, mHandler, mRegister);
    	mypage.setVisible(true);
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.TextField textField2;
    private java.awt.TextField textField3;
    private javax.swing.JButton jButton5;

    @Override
    public void nodeClicked(MapViewer.NodeInfo n){
        Sheep sheep = mainMapLogic.getSheepByDot(n.getDot());
        initFromMap(sheep);
        this.sheepName = sheep.getName();
        info(this.sheepName);
        initChosen();
    }

    @Override
    public void mapClicked(double x, double y){

    }
}
