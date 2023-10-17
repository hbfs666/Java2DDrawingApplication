/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private JPanel firstLine;
    private JPanel secondLine;
    private JPanel topPanel;

    // create the widgets for the firstLine Panel.
    private JLabel shapetxt;
    private JComboBox<String> cb1;
    private JButton firstColorb;
    private JButton secondColorb;
    private JButton undo;
    private JButton clear;
    private static final String[] shapesop = {"Rectangle","Line","Oval"};
    
    //create the widgets for the secondLine Panel.
    private JLabel options;
    private JCheckBox filled;
    private JCheckBox useGradient;
    private JCheckBox dashed;
    private JLabel widthtxt;
    private JSpinner lineWidths;
    private JLabel lengthtxt;
    private JSpinner lineLengths;
    
    
    // Variables for drawPanel.
    private Color firstColor= Color.LIGHT_GRAY;
    private Color secondColor= Color.LIGHT_GRAY;
    private ArrayList<Color> colors;
    private DrawPanel drawPanel;
    private int lineWidth;
    private float lineLength;
    private BasicStroke stroke;
    private Paint paint;
    ArrayList<MyShapes> shapes = new ArrayList();
    
    // add status label
    private JLabel status;
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        // add widgets to panels
        firstLine = new JPanel();
        firstLine.setLayout(new FlowLayout());
        firstLine.setBackground(Color.CYAN);
        secondLine = new JPanel();
        secondLine.setLayout(new FlowLayout());
        secondLine.setBackground(Color.CYAN);
        
        // firstLine widgets
        shapetxt = new JLabel("Shape:");
        cb1 = new JComboBox<>(shapesop);
        firstColorb = new JButton("1st Color...");
        secondColorb = new JButton("2nd Color...");
        undo = new JButton("Undo");
        clear = new JButton("Clear");
        
        firstLine.add(shapetxt);
        firstLine.add(cb1);
        firstLine.add(firstColorb);
        firstLine.add(secondColorb);
        firstLine.add(undo);
        firstLine.add(clear);   

        // secondLine widgets
        options = new JLabel("Options:");
        filled = new JCheckBox("Filled");
        useGradient = new JCheckBox("Use Gradient");
        dashed = new JCheckBox("Dashed");
        widthtxt = new JLabel("Line Width:");
        lineWidths = new JSpinner(new SpinnerNumberModel(10,1,99,1));
        lengthtxt = new JLabel("Dash Length:");
        lineLengths = new JSpinner(new SpinnerNumberModel(10,1,99,1));
        
        secondLine.add(options);
        secondLine.add(filled);
        secondLine.add(useGradient);
        secondLine.add(dashed);
        secondLine.add(widthtxt);
        secondLine.add(lineWidths);
        secondLine.add(lengthtxt);
        secondLine.add(lineLengths);

        // add top panel of two panels
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));

        topPanel.add(firstLine);
        topPanel.add(secondLine);

        // add topPanel to North, drawPanel to Center, and statusLabel to South
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        drawPanel = new DrawPanel();
        drawPanel.setBackground(Color.WHITE);
        add(drawPanel, BorderLayout.CENTER);
        status = new JLabel("");
        status.setBackground(Color.GRAY);
        add(status, BorderLayout.SOUTH);
        
        //add listeners and event handlers
        firstColorb.addActionListener(
            new ActionListener()
            {
             @Override
             public void actionPerformed(ActionEvent event){
                firstColor = JColorChooser.showDialog(
                    DrawingApplicationFrame.this,"Choose a color",firstColor);

                if (firstColor == null)
                    firstColor = Color.LIGHT_GRAY;
             }
            }
        );
        secondColorb.addActionListener(
            new ActionListener()
            {
             @Override
             public void actionPerformed(ActionEvent event){
                secondColor = JColorChooser.showDialog(
                    DrawingApplicationFrame.this,"Choose a color",secondColor);

                if (secondColor == null)
                    secondColor = Color.LIGHT_GRAY;
             }
            }
        );
        
        undo.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent event){
                    shapes.remove(shapes.size()-1);
                    drawPanel.repaint();
                }
            }
        );
        
        clear.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent event){
                    shapes = new ArrayList();
                    drawPanel.repaint();
                }
            }
        );
        
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        private Point start;
        private Point temp;
        private Point end;
        

        public DrawPanel()
        {
            //setBackground(Color.WHITE);
            addMouseMotionListener(new MouseHandler());
            addMouseListener(new MouseHandler());
           
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape: shapes)
                shape.draw(g2d);
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                start = event.getPoint();
                
                lineWidth=(Integer)lineWidths.getValue();
                lineLength=(Integer)lineLengths.getValue();
                float dash[] = {lineLength};
                
                if (dashed.isSelected())
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 0);
                } else
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                
                if (useGradient.isSelected()){
                    paint = new GradientPaint(0,0, firstColor,50,50,secondColor,true);
                }else
                {
                    paint = new GradientPaint(0,0, firstColor,50,50,firstColor,true);
                }
                
                
                //
                if ("Line".equals(shapesop[cb1.getSelectedIndex()])){
                    shapes.add(new MyLine(start,start,paint,stroke));
//                    drawPanel.repaint();
                }
                else if ("Oval".equals(shapesop[cb1.getSelectedIndex()])){
                    shapes.add(new MyOval(start,start,paint,stroke,filled.isSelected()));
//                    drawPanel.repaint();
                }
                else if ("Rectangle".equals(shapesop[cb1.getSelectedIndex()])){
                    shapes.add(new MyRectangle(start,start,paint,stroke,filled.isSelected()));
//                    drawPanel.repaint();
                }
                else{
                    System.out.println("Error Matching");
                }
                
                drawPanel.repaint();
                
            }
            
            @Override
            public void mouseReleased(MouseEvent event)
            {
                end = event.getPoint();
                
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                temp = event.getPoint();
                if(shapes!=null){
                    shapes.get(shapes.size()-1).setEndPoint(temp);
                    drawPanel.repaint();
                }
                
//                if ("Line".equals(shapesop[cb1.getSelectedIndex()])){
//                    shapes[shapes.size()-1] = new MyOval(start,temp,paint,stroke,filled.isSelected());
//                    drawPanel.repaint();
//                }
//                if ("Oval".equals(shapesop[cb1.getSelectedIndex()])){
//                    shapes.add(new MyOval(start,temp,paint,stroke,filled.isSelected()));
//                    drawPanel.repaint();
//                }
//                if ("Rectangle".equals(shapesop[cb1.getSelectedIndex()])){
//                    shapes.add(new MyRectangle(start,temp,paint,stroke,filled.isSelected()));
//                    drawPanel.repaint();
//                }
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                status.setText(String.format("(%d, %d)",event.getX(), event.getY()));
            }
        }

    }
}
