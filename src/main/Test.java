package main;

import javax.swing.*;

public class Test extends JFrame{

    public static void main(String[] args ) throws Exception{
        new Test();
    }

    public Test() throws Exception{
       /*DatabaseHandler handler = new DatabaseHandler();
        int farmerid = handler.authenticate("farm", "farm");

        Register mRegister = new Register(handler, farmerid);

        AddSheep addsheep = new AddSheep(new farmerid, handler, mRegister);
        addsheep.setVisible(true);

        //MainMenu chosenSheep = new MainMenu(4, handler, mRegister);
        //chosenSheep.setVisible(true);

        //EditSheep editSheep = new EditSheep("tormod", handler, mRegister);
        //editSheep.setVisible(true);

         /*MapViewer map = new MapViewer();

        final MapSheeps mapSheep = new MapSheeps(handler, mRegister, farmerid, map);

        setSize(700, 700);
        add(map.getMap(), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelBottom = new JPanel();
        final JLabel sheepNames = new JLabel();
        panelBottom.add(sheepNames);
        add(panelBottom, BorderLayout.SOUTH);

        //Listener to keep track of nodes.
        map.addListener(new MapViewer.MapViewerListener() {
            @Override
            public void nodeClicked(MapViewer.NodeInfo n) {
                int sheepId = mapSheep.getMapListenerSheepID();
                String name = "name: " + n.getNodeName() +", " +"id: " + sheepId;
                sheepNames.setText(name);
            }

            @Override
            public void nodeDoubleClicked() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

        });

        setVisible(true);  */
    }
}
