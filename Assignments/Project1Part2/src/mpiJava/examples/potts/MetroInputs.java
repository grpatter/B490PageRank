import java.awt.*;
import java.awt.event.*;
//import java.applet.*;
import java.io.*;
import java.util.*;
 
 
public class MetroInputs extends Panel implements 
             ItemListener, AdjustmentListener {

  Metrop outerparent;

  GridBagLayout gbl;
  GridBagConstraints gbc;  

  static public Label pottsLabel;  
  static public Label nprocsLabel;    static public Choice nprocsChoice;
  static public TextField nprocsTxt;
  static public Label xnprocsLabel;    static public Choice xnprocsChoice;
  static public Label ynprocsLabel;    static public Choice ynprocsChoice;
  static public TextField ynprocsTxt;
  static public Label statesLabel;    static public Choice statesChoice;
  static public Label lsizeLabel;     static public Choice lsizeChoice;
  static public Label tsweepsLabel;   static public TextField tsweepsTxt;  
  static public Label msweepsLabel;   static public TextField msweepsTxt;  
  static public Label binsizeLabel;   static public TextField binsizeTxt;  
  static public Label numbinLabel;    static public TextField numbinTxt;  
  static public Label measureLabel;    static public TextField measureTxt;  
  static public Label betaLabel;      //static public Choice betaChoice;
  static public Scrollbar betaScroll; static public TextField betaTxt;
  static public Label orderLabel;     static public Choice orderChoice;
  static public Label rseedLabel;     static public TextField rseedTxt;
  static public Label spinsizeLabel;  static public TextField spinsizeTxt;
  static public Scrollbar spinsizeScroll;
  static public Label distLabel;      static public Choice distChoice;
  static public Label updateLabel;    static public Choice updateChoice;


    //static final int max = 10000000;
    static final int max = 10000;
 
  ////////////////////////////////////////////////////////////  
  private void add(Component c, GridBagLayout gbl,
		   GridBagConstraints gbc, 
                   int x, int y, int w, int h) {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = w;
    gbc.gridheight = h;
    gbl.setConstraints(c,gbc);
    add(c);
  }
  ////////////////////////////////////////////////////////////
  public void paint(Graphics g) {
    Dimension d = getSize();
    Insets in = getInsets();
 
    g.drawRect(2, 2, d.width-6, d.height-6);
 
  }
  ////////////////////////////////////////////////////////////
  public Insets getInsets() {
    return new Insets(10,10,10,10);
  }

  ////////////////////////////////////////////////////////////
  public void adjustmentValueChanged(AdjustmentEvent evt) {

    if(evt.getSource() == betaScroll) 
      betaTxt.setText(Float.toString((float)betaScroll.getValue()/max));
    else
      if(evt.getSource() == spinsizeScroll) {
	spinsizeTxt.setText(Integer.toString((int)spinsizeScroll.getValue()));
	MonteGraphics.spinSize = spinsizeScroll.getValue();
	this.outerparent.update();
      }
  }


  ////////////////////////////////////////////////////////////
  public void itemStateChanged(ItemEvent ievt) {

    if(ievt.getStateChange() == ItemEvent.SELECTED) {
      //System.out.println("lsizeChoice= "+lsizeChoice.getSelectedItem());
      if(ievt.getSource() == xnprocsChoice) {
	CommVars.procdim[0] = Integer.valueOf
	   (xnprocsChoice.getSelectedItem()).intValue();
	CommVars.procdim[1] = CommVars.nprocs/Integer.valueOf
	   (xnprocsChoice.getSelectedItem()).intValue();
	ynprocsTxt.setText(Integer.toString(CommVars.procdim[1]));	
      }
      else if(ievt.getSource() == lsizeChoice) {
	int gsize = Integer.valueOf
	  (lsizeChoice.getSelectedItem()).intValue();
	CommVars.global_spin = new int[gsize][gsize];
	for(int i = 0; i < gsize; i++)
	  for(int j = 0; j < gsize; j++) 
	    CommVars.global_spin[i][j] = 0;
      }
      else if(ievt.getSource() == statesChoice) {
	MonteGraphics.spinColr = Integer.valueOf
	  (statesChoice.getSelectedItem()).intValue();
      }
      else if(ievt.getSource() == updateChoice) {
	//CommVars.updateflag = updateChoice.getSelectedIndex();
      }

      this.outerparent.update();
    }

  }


  ////////////////////////////////////////////////////////////
  public MetroInputs(Metrop target) {

    this.outerparent = target;

    gbl = new GridBagLayout();
    setLayout(gbl);
    
    pottsLabel = new Label("2-D  Q-state  Potts  Spin  Model  :  mpiJava  version",Label.CENTER);
    pottsLabel.setFont(new Font("TimesRoman", Font.BOLD, 18));
    pottsLabel.setBackground(Color.yellow);
    pottsLabel.setForeground(Color.blue);
 
    setFont(new Font("Helvetica", Font.PLAIN, 12));

    nprocsLabel = new Label("Total Nodes :",Label.RIGHT);
    nprocsTxt = new TextField(3);
    nprocsTxt.setText(Integer.toString(CommVars.nprocs));
    nprocsTxt.setEditable(false);

    xnprocsLabel = new Label("Nodes in X dir:",Label.RIGHT);
    xnprocsChoice = new Choice();
    for(int i = 1; i <= CommVars.nprocs; i=i*2)  
      xnprocsChoice.addItem(Integer.toString(i));
    xnprocsChoice.select(Integer.toString(CommVars.procdim[0]));
    xnprocsChoice.addItemListener(this);

    ynprocsLabel = new Label("Nodes in Y dir:",Label.RIGHT);
    ynprocsTxt = new TextField(3);
    ynprocsTxt.setText(Integer.toString(CommVars.procdim[1]));
    ynprocsTxt.setEditable(false);

    statesLabel = new Label("Q-states :",Label.RIGHT);
    statesChoice = new Choice();
    statesChoice.addItem("2");
    statesChoice.addItem("3");
    statesChoice.addItem("4");
    statesChoice.addItem("5");
    statesChoice.addItem("6");
    statesChoice.addItem("7");
    statesChoice.addItem("8");
    statesChoice.addItem("9");
    statesChoice.addItem("10");
    statesChoice.addItem("11");
    statesChoice.addItem("12");
    statesChoice.addItemListener(this);
 
    lsizeLabel = new Label("Lattice Size:",Label.RIGHT);
    lsizeChoice = new Choice();
    lsizeChoice.addItem("8");
    lsizeChoice.addItem("16");
    lsizeChoice.addItem("32");
    lsizeChoice.addItem("64");
    lsizeChoice.addItem("128");
    lsizeChoice.addItem("256");
    lsizeChoice.addItemListener(this);

    CommVars.global_spin = new int[8][8];
    
    tsweepsLabel = new Label("Thermalization Sweeps :",Label.RIGHT);
    tsweepsTxt = new TextField(7);

    msweepsLabel = new Label("Sweeps per Measurement :",Label.RIGHT);
    msweepsTxt = new TextField("1", 3);
    //msweepsTxt.setText("10");

    measureLabel = new Label("Measurement :",Label.RIGHT);
    measureTxt = new TextField("100",7);
    
    binsizeLabel = new Label("Bin Size :",Label.RIGHT);
    binsizeTxt = new TextField("2",7);
    
    numbinLabel = new Label("Number of Bins :",Label.RIGHT);
    numbinTxt = new TextField("10",7);

    betaLabel = new Label("Beta (inverse temperature) :",Label.RIGHT);
    //betaTxt = new TextField("0.4406868", 7);    
    betaTxt = new TextField("0.825", 7);    
    betaScroll = new Scrollbar(Scrollbar.HORIZONTAL,0,0,0,max);
    //betaScroll.setValue(4406868);
    betaScroll.setValue(8250);
    //betaScroll.setBlockIncrement(100000);
    //betaScroll.setUnitIncrement(1000000);
    betaScroll.setBlockIncrement(1000);
    betaScroll.setUnitIncrement(1000);
    betaScroll.addAdjustmentListener(this);
    //betaChoice = new Choice();
    //betaChoice.addItem("0.4406868");


    
    orderLabel = new Label("Ordered or Random Start?",Label.RIGHT);
    orderChoice= new Choice();
    orderChoice.addItem("ordered");
    orderChoice.addItem("random");
    
    //rseedLabel = new Label("Seed for the Random Number Generator",Label.RIGHT);
    rseedLabel = new Label("Seed :",Label.RIGHT);
    rseedTxt = new TextField("0", 7);
    //rseedTxt.setText("0");

    distLabel = new Label("Distribute :",Label.RIGHT);
    distChoice= new Choice();
    distChoice.addItem("Block");
    distChoice.addItem("Column");

    
    spinsizeLabel = new Label("Graphic: Spin Oval Size :",Label.RIGHT);
    spinsizeTxt = new TextField("8", 3);
    spinsizeScroll = new Scrollbar(Scrollbar.HORIZONTAL,0,0,0,50);
    //spinsizeScroll.setVisibleAmount(10);
    spinsizeScroll.setValue(8);
    spinsizeScroll.setBlockIncrement(1);
    spinsizeScroll.setUnitIncrement(1);
    spinsizeScroll.addAdjustmentListener(this);

    updateLabel = new Label("Graphic: Update by",Label.RIGHT);

    updateChoice= new Choice();
    updateChoice.addItem("sweep");
    updateChoice.addItem("column");
    updateChoice.addItem("spin");
    updateChoice.addItemListener(this);

    Label blankLabel = new Label(" ");     
    
    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 20;
    gbc.weighty = 100;
    gbc.ipadx = 1;
    gbc.ipady = 1;
    
    //gbc.anchor = GridBagConstraints.SOUTH;
    //add(dataLabel,       gbl, gbc, 1, 0, 2, 1);
    //add(blankLabel,      gbl, gbc, 0, 0, 1, 1);

    add(pottsLabel,  gbl, gbc, 1, 1, 7, 1);

    add(nprocsLabel,  gbl, gbc, 1, 2, 1, 1);
    add(nprocsTxt,    gbl, gbc, 2, 2, 1, 1);

    add(xnprocsLabel,  gbl, gbc, 1, 3, 1, 1);
    add(xnprocsChoice, gbl, gbc, 2, 3, 1, 1);

    add(ynprocsLabel,  gbl, gbc, 1, 4, 1, 1);
    add(ynprocsTxt,    gbl, gbc, 2, 4, 1, 1);

    add(statesLabel,  gbl, gbc, 1, 5, 1, 1);
    add(statesChoice, gbl, gbc, 2, 5, 1, 1);

    ///////////////////////////////////////////////    
    
    add(lsizeLabel,   gbl, gbc, 3, 2, 1, 1);
    add(lsizeChoice,  gbl, gbc, 4, 2, 1, 1);

    //add(distLabel,    gbl, gbc, 1, 5, 1, 1);
    //add(distChoice,   gbl, gbc, 2, 5, 1, 1);

    //add(tsweepsLabel, gbl, gbc, 1, 5, 1, 1);
    //add(tsweepsTxt,   gbl, gbc, 2, 5, 1, 1);

    add(msweepsLabel, gbl, gbc, 3, 3, 1, 1);
    add(msweepsTxt,   gbl, gbc, 4, 3, 1, 1);

    add(measureLabel, gbl, gbc, 3, 4, 1, 1);
    add(measureTxt,   gbl, gbc, 4, 4, 1, 1);    

    //add(binsizeLabel, gbl, gbc, 3, 3, 1, 1);
    //add(binsizeTxt,   gbl, gbc, 4, 3, 1, 1); 
    
    //add(numbinLabel,  gbl, gbc, 3, 4, 1, 1);
    //add(numbinTxt,    gbl, gbc, 4, 4, 1, 1); 
    
    add(rseedLabel,   gbl, gbc, 3, 5, 1, 1);
    add(rseedTxt,     gbl, gbc, 4, 5, 1, 1);

    ///////////////
    add(betaLabel,    gbl, gbc, 5, 2, 1, 1);
    add(betaTxt,      gbl, gbc, 6, 2, 1, 1);
    add(betaScroll,   gbl, gbc, 7, 2, 1, 1);
    
    add(orderLabel,   gbl, gbc, 5, 3, 1, 1);
    add(orderChoice,  gbl, gbc, 6, 3, 1, 1);

    add(updateLabel,  gbl, gbc, 5, 4, 1, 1);
    add(updateChoice, gbl, gbc, 6, 4, 1, 1);

    add(spinsizeLabel,    gbl, gbc, 5, 5, 1, 1);
    add(spinsizeTxt,      gbl, gbc, 6, 5, 1, 1);
    add(spinsizeScroll,   gbl, gbc, 7, 5, 1, 1);
    
    //add(blankLabel,      gbl, gbc, 2, 1, 2, 1);
  }


  //public void getInput() {
  
    



  
}  


