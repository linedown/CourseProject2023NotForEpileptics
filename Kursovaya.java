/* �������� ����������� ������ �.�. �� ������ ���-111. ������� 27.
	�������� ������. ����� 2. ��������.
	���������� �2 ��������������� �� ������ ���������� �1. ������������ ������ � ������
	��� ��������� (��������, �������� ������������ ��������� �� ������������ ����������)
	����� ���� ������ �����������, �� ������ ��������������� �������� ���� ���������
	� ���������� ���� �� ��������� ��������������� ����������.
*/

import java.awt.*;
import java.awt.event.*;

class InsetsPanel extends Panel{
    int top, left, bottom, right;

    InsetsPanel(int top, int left, int bottom, int right){
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public Insets getInsets(){
        return new Insets(top, left, bottom, right);
    }
}

// �������� ����� ����������, ���������� ���������� Runnable
public class Kursovaya extends Panel implements AdjustmentListener, ItemListener, Runnable{
    Panel changePanel;
    Canvas resultPanel;
    Panel fontAndStylePanel;
    Panel listAndTextPanel;
    Panel radioButtonAndScrollbarPanel;
    InsetsPanel fontPanel;
    InsetsPanel stylePanel;
	InsetsPanel effectPanel;
    Panel listPanel;
    InsetsPanel textPanel;
    Panel radioPanel;
    Panel labelSizePanel;
    Panel sbSizePanel;
    Panel labelVertPanel;
    Panel sbVertPanel;
    Panel labelHorPanel;
    Panel sbHorPanel;
    Choice fontChoice;
    Choice styleChoice;
    List colorTextList;
    TextField textLine;
    CheckboxGroup colorCbg;
    Checkbox colorCb1;
    Checkbox colorCb2;
    Checkbox colorCb3;
    Checkbox colorCb4;
    Label sizeLabel;
    Label translateVertLabel;
    Label translateHorLabel;
	Label effectLabel;
    Scrollbar sizeSb;
    Scrollbar translateVertSb;
    Scrollbar translateHorsizeSb;
    String fontStr;
    String styleStr;
    int fontNum;
	// ���������� �����, ������� ����� ��������������
	// ��� ���������� ������� ��������
    int perText;
    int incr;
    int sizeMax;
    double rotateValue;
    int changeNum;
    byte backWhere100;
	byte incr_ot_0_do_50;
	boolean stopFlag;
	boolean transition;
	boolean downFlag;
	// ���������� ����� ���� Thread - �������, �� �������
	// ����� ���������� ������ �������� � ������������� �� �������� ��������
	Thread curThread1 = null;
    Thread curThread2 = null;
    public Kursovaya(){
		// ������������� �����
		// ����, ������������ ��� ��������
		// ���������� ������� ������� �� 2 � �� 3.
		// ���������� ��� ���������� ����� ������
		// �������� � ������� �����.
		changeNum = 0;
		// ����-���������. ������������ � 1 ����� ��������.
		// �� � ���������� � ���� ����������� �������� ��������
		// ������.
		incr = 1;
		// ��������, ������������ ������ rotate
		// ������� ���� Graphics2D. ���������� � ��������
		// ������ �������.
		rotateValue = 0;
		// ����, �������� �������� ��������� ��� ��������� �����.
		perText = 0;
		// ����-�������, ���������� �� 0 �� 100. ������������
		// �� 2 ����� ��������. ���������� �� � ����������.
		backWhere100 = 0;
		// ���� ���� byte, ���������� �� 0 �� 50 � �� 50 �� 0. ����������
		// ��� �������� ����������� ����� �� 2 ����� ��������.
		incr_ot_0_do_50 = 0;
		// ����, ������������ ��� ������ ������� ���������
		// � ������������� ������ �������.
		stopFlag = false;
		// ����, ������������ ��� �������� �������� � ������ ������
		// �� ������.
		transition = false;
		// ����, ����������� ��� ��������� ��������� �������� ������
		// �������.
		downFlag = false;
		
        setLayout(new GridLayout(1, 2));
        changePanel = new Panel(new GridLayout(3, 1));
		
		// ������-����� �� ������ ���������� ����������� ������
        resultPanel = new Canvas(){
			// ��������� � �������������� ������� ����������� 
			//(��� �������������� ��������)
            Graphics2D g2d;
            Image img;

            public void update(Graphics g){
                paint(g);
            }
            public void paint(Graphics g){
                img = createImage(getSize().width, getSize().height);
                g2d = (Graphics2D)img.getGraphics();
                Graphics2D g2 = (Graphics2D)g;
                drawStr();
                g2.drawImage(img, 0, 0, this);
            }
			// �����, ���������� �� ��������� ����� �� ������-������
            private void drawStr(){
				int y = 25;
				// ��������� ���������� �������� � ������� ������ 
				// getText � ���� textLine ���� TextField
                String textStr = textLine.getText();
                if("Italic".equals(styleStr)) fontNum = Font.ITALIC;
                else if("Plain".equals(styleStr)) fontNum = Font.PLAIN;
                else fontNum = Font.BOLD;
                g2d.setFont(new Font(fontStr, fontNum, (sizeSb.getValue() + 15) / 2));
				// ��������� �����-��������� � ������ ������� ���� ������.
				g2d.drawString("������� ��", getSize().width - 135, y);
				g2d.drawString("������ ����", getSize().width - 135, y += 20);
				g2d.drawString("��� �������� ���", getSize().width - 135, y += 20);
				g2d.drawString("�������������", getSize().width - 135, y += 20);
				g2d.drawString("�������.", getSize().width - 135, y += 20);
				// �������� ��������� ������ �� ���� rotateValue (� ��������) ������������
				// ����� ������ ������-������ ���������� �� 20 �� ����� ����.
                g2d.rotate(rotateValue, (getSize().width - 20) / 2, (getSize().height - 20) / 2);
                if(!transition){
					// ���� transition = false, ����������� ��������� ��� 1 ����� �������� -
					// ��������� ����� ������, � ���������� ����������� ������������ x � y.
					// ����� ���� �������� ���������� ��������� ��������� �������� ���� rotateValue.
                    g2d.drawString(textStr, 5 + perText, 20 + perText);
                } else{
					// ��� ����������� �������� � ������� �������� �����������������
					// ��� ����� ���� � ���������������� ��������������� � ������ for ����
					/*g2d.translate(Math.random() * 2, Math.random() * 2);
					g2d.translate(incr_ot_0_do_50 / 2.5, 0);*/
					// ����� ���������� ������������ ��������� ����������� �������� ��������.
					// ������: 
					if(downFlag){
						// ���� downFlag = true, �� ���������� ��������� ����� ����� - �� ����
						// ��� ������ ������ � �������, ��� ���� �� ����������.
						// ������������ ������������.
						for(int i = 0; i < textStr.length(); i++){
							// �������� ����� ���������� ������� ������� ���������� �����
							// translate, ���� ���������� ��������� � ������� ������ 
							// random ������ Math. ������ ����� ���������� � ����������
							// ��������������� ����� � ��������� ������� � ��������� [0, 1).
							// ��� ��������� �� 2 ����� �������� ����� � ��������� [0, 2).
							// ��������� ����, ��� ������ ��� ��� ��������� ������������
							// ��������� �������� �������� � ������� �������, �����������
							// ������ �������� ��������. ��� ��������� �����������
							// �� ��������������� ����� �� ��������� ������� �������� ��
							// ��������� ��������� �����.
							g2d.translate(Math.random() * 2, Math.random() * 2);
							// ������� ����������� �����-�������� ������-�����, ���������
							// �������� ������������� ����-�������� incr_ot_0_do_50
							g2d.translate(incr_ot_0_do_50 / 2.5, 0);
							// ����������, ����� ��������� �������� � ���� ������. ��� ��� 1 ��������
							// ������ ���� ����� String, ����� ���������� ������������ �����������
							// ������� �� ������� (� ������� ������ charAt) � ������ �������.
							// ��� ����� ��������� ������� ����������� ���� char � ���� String.
							//g2d.drawString("" + textStr.charAt(i), 5 + perText + 15 * i, 20 + perText + 10 * i);
							g2d.drawString("" + textStr.charAt(i), 5 + perText + 15 * i, 20 + perText + (getWidth() * incr_ot_0_do_50 * i) / (2 * getHeight()));
						}
                    } else{
						// ���� downFlag = true, �� ���������� ��������� ������ ���� - �� ����
						// ��� ������ ������ � �������, ��� ���� �� ����������.
						for(int i = 0; i < textStr.length(); i++){
							g2d.translate(Math.random() * 2, Math.random() * 2);
							g2d.translate(incr_ot_0_do_50 / 2.5, 0);
							//g2d.drawString("" + textStr.charAt(i), 5 + perText + 5 * i, 20 + perText - 20 * i);
							g2d.drawString("" + textStr.charAt(i), 5 + perText + 5 * i, 20 + perText - (getWidth() * incr_ot_0_do_50 * i) / (2 * getHeight()) );
						}
					}
                }
            }

        };
		// ���������� �������� ������� �� ���� ��� ������-������
		resultPanel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				stopFlag = !stopFlag;
				// ��� ������� �� ���/��� ����� ���������� ����� 
				// ������� stopThreads � startThreads � ����������� �� ����,
				// � ����� ��������� ��������� �������� ���� stopFlag ����
				// boolean.
				// ��� ��������� ������� ���������� ����� stopThreads,
				// � ������� ����� curThread1 � curThread2 ���� Thread
				// ����� ��������� ������� ������ (null)
				if(stopFlag) stopThreads();
				// �������� ������� - ������ �������, ��������������� ��� �����
				// ����� ������ startThreads, � ������� ���������� �������� 
				// ����������� ������� Thread � ������������� ����� curThread1 � curThread2
				// ���������� ������������.
				else startThreads();
			}
		});
		
        resultPanel.setBackground(new Color(104, 88, 243));
        resultPanel.setForeground(Color.black);
        resultPanel.setFont(new Font("Dialog", Font.BOLD, 13));

        fontAndStylePanel = new Panel(new GridLayout(1, 2));
        listAndTextPanel = new Panel(new GridLayout(1, 2));

        radioButtonAndScrollbarPanel = new Panel(new GridLayout(7, 1));
        radioPanel = new Panel(new GridLayout());
        labelSizePanel = new Panel(new BorderLayout());
        sbSizePanel = new Panel(new GridLayout());
        labelVertPanel = new Panel(new BorderLayout());
        sbVertPanel = new Panel(new GridLayout());
        labelHorPanel = new Panel(new BorderLayout());
        sbHorPanel = new Panel(new GridLayout());
        radioPanel.setBackground(new Color(155, 155, 155));
        labelSizePanel.setBackground(new Color(155, 155, 155));
        sbSizePanel.setBackground(new Color(155, 155, 155));
        labelVertPanel.setBackground(new Color(155, 155, 155));
        labelHorPanel.setBackground(new Color(155, 155, 155));
        sbHorPanel.setBackground(new Color(155, 155, 155));
        sbVertPanel.setBackground(new Color(155, 155, 155));

        fontPanel = new InsetsPanel(20, 20, 0, 20);
        fontPanel.setBackground(new Color(105, 105, 105));

        stylePanel = new InsetsPanel(20, 20, 0, 20);
        stylePanel.setBackground(new Color(155, 155, 155));

        listPanel = new Panel(new GridLayout(1, 1));
		
        textPanel = new InsetsPanel(20, 0, 0, 0);
        textPanel.setBackground(new Color(105, 105, 105));

        fontChoice = new Choice();
        fontChoice.add("Dialog");
        fontChoice.add("DialogInput");
        fontChoice.add("Serif");
        fontChoice.add("SansSerif");
        fontChoice.add("Monospaced");
        fontChoice.addItemListener(this);
        fontChoice.setEnabled(false);

        styleChoice = new Choice();
        styleChoice.add("Bold");
        styleChoice.add("Plain");
        styleChoice.add("Italic");
        styleChoice.addItemListener(this);
        styleChoice.setEnabled(false);

        colorTextList = new List(8);
        colorTextList.add("������");
        colorTextList.add("�����");
        colorTextList.add("�����");
        colorTextList.add("�������");
        colorTextList.add("�������");
        colorTextList.add("���������");
        colorTextList.add("������");
        colorTextList.add("�����");
        colorTextList.addItemListener(this);
        colorTextList.setEnabled(false);

        textLine = new TextField("Java");
		textLine.setColumns(15);
        textLine.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e){
                resultPanel.repaint();
            }
        });

        colorCbg = new CheckboxGroup();
        colorCb1 = new Checkbox("�������", colorCbg, false);
        colorCb2 = new Checkbox("��������", colorCbg, false);
        colorCb3 = new Checkbox("�����", colorCbg, false);
        colorCb4 = new Checkbox("�������", colorCbg, true);
        colorCb1.addItemListener(this);
        colorCb1.setEnabled(false);
        colorCb2.addItemListener(this);
        colorCb2.setEnabled(false);
        colorCb3.addItemListener(this);
        colorCb3.setEnabled(false);
        colorCb4.addItemListener(this);
        colorCb4.setEnabled(false);

        sizeLabel = new Label("�������", Label.CENTER);
        sizeSb = new Scrollbar(Scrollbar.HORIZONTAL, 15, 1, 1, 90);
        sizeSb.setEnabled(false);
        translateVertLabel = new Label("����������� �� ���������", Label.CENTER);
        translateVertSb = new Scrollbar(Scrollbar.HORIZONTAL, 135, 1, 1, 300);
        translateVertSb.setEnabled(false);
        translateHorLabel = new Label("����������� �� �����������", Label.CENTER);
        translateHorsizeSb = new Scrollbar(Scrollbar.HORIZONTAL, 130, 1, 1, 300);
        translateHorsizeSb.setEnabled(false);

        sizeSb.addAdjustmentListener(this);
        translateVertSb.addAdjustmentListener(this);
        translateHorsizeSb.addAdjustmentListener(this);

        fontPanel.add(fontChoice);

        stylePanel.add(styleChoice);
        listPanel.add(colorTextList);
        textPanel.add(textLine);
        radioPanel.add(colorCb1);
        radioPanel.add(colorCb2);
        radioPanel.add(colorCb3);
        radioPanel.add(colorCb4);
        labelSizePanel.add(sizeLabel);
        sbSizePanel.add(sizeSb);
        labelVertPanel.add(translateVertLabel);
        sbVertPanel.add(translateVertSb);
        labelHorPanel.add(translateHorLabel);
        sbHorPanel.add(translateHorsizeSb);

        radioButtonAndScrollbarPanel.add(radioPanel);
        radioButtonAndScrollbarPanel.add(labelSizePanel);
        radioButtonAndScrollbarPanel.add(sbSizePanel);
        radioButtonAndScrollbarPanel.add(labelVertPanel);
        radioButtonAndScrollbarPanel.add(sbVertPanel);
        radioButtonAndScrollbarPanel.add(labelHorPanel);
        radioButtonAndScrollbarPanel.add(sbHorPanel);
		
        fontAndStylePanel.add(fontPanel);
        fontAndStylePanel.add(stylePanel);
        listAndTextPanel.add(listPanel);
        listAndTextPanel.add(textPanel);
        changePanel.add(fontAndStylePanel);
        changePanel.add(listAndTextPanel);
        changePanel.add(radioButtonAndScrollbarPanel);
        add(changePanel);
        add(resultPanel);
    }

    public void adjustmentValueChanged(AdjustmentEvent e){
        resultPanel.repaint();
    }

    public void itemStateChanged(ItemEvent e){
        String s = "" + e.getItem();
        if("Dialog".equals(s) || "DialogInput".equals(s) || "Serif".equals(s) || "SansSerif".equals(s) || "Monospaced".equals(s)) fontStr = s;
        else if("Bold".equals(s) || "Plain".equals(s) || "Italic".equals(s)) styleStr = s;
        else{
            if("�������".equals(s)) resultPanel.setBackground(new Color(104, 88, 243));
            else if("�����".equals(s)) resultPanel.setBackground(new Color(0, 0, 153));
            else if("��������".equals(s)) resultPanel.setBackground(new Color(144, 0, 0));
            else if("�������".equals(s)) resultPanel.setBackground(new Color(0, 153, 0));
            else if("0".equals(s)) resultPanel.setForeground(Color.black);
            else if("1".equals(s)) resultPanel.setForeground(Color.gray);
            else if("2".equals(s)) resultPanel.setForeground(Color.blue);
            else if("3".equals(s)) resultPanel.setForeground(Color.red);
            else if("4".equals(s)) resultPanel.setForeground(Color.green);
            else if("5".equals(s)) resultPanel.setForeground(Color.orange);
            else if("6".equals(s)) resultPanel.setForeground(Color.yellow);
            else if("7".equals(s)) resultPanel.setForeground(Color.white);
        }
        resultPanel.repaint();
    }
	// ���������� ������ run ���������� Runnable.
    public void run(){
		// ��������� ������ �� ������� �����.
        Thread curThread = Thread.currentThread();
		// ���� ������ ����� �������, ���������� �����
		// ������ ����������� ������ ������ � ������ moveString1,
		// ���������� �� 1 ����� ��������.
        while(curThread1 == curThread) {
            resultPanel.repaint();
            moveString1();
            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {}
        }
    }
	// ������ moveString1 � moveString2 �������� �������� ������ 
	// ��� ���������� ���������� �������� � ��������� �������,
	// ������������ �� ������-����� resultPanel.
	
	// ����� moveString1
    private void moveString1() {
		// ���� ���� transition = true, ���������� ����� ������ wait -
		// �� ���� ����� ����� ���������� � ��������� ��������.
		// ������� ����, ��� ���������� ����� wait ����������� ������
		// ���� ������������������ (� ������� ��������� synchronized),
		// ����� ����� ���������� ����������� ����������� ��� �������.
		// � ������������ ������� ����� ���������� ����������
		// InterruptedException. ��� ����� ������� � ����� ��������,
		// ��������� ���� � ����� catch.
        while (transition){
            try{
                synchronized (this){
                    wait();
                }
            } catch (InterruptedException e) {}
        }
		// ������������ ��������, �������� ����� ��������� ���� perText, ������������ ������
		// �� ����������� ������ � ������ ������-������. ���������� ���������� �� ���� ��������.
        sizeMax = (resultPanel.getWidth() >= resultPanel.getHeight()) ? resultPanel.getHeight() - 60 : resultPanel.getWidth() - 60;
		// ���� �������� ���� perText ����� ������ 0 ��� ������ sizeMax,
        if (perText >= sizeMax || perText < 0) {
			// ���������� ���������� �������� ���� changeNum, ����������� �� ��������� �����
			// ������ (��������� ����) � ������� ����,
            changeNum++;
			// � incr ����������� �������� �������� ������.
            incr = -incr;
			// ���������� �������� ������� �� ������� �� 3 �������� ���� changeNum.
			// �� ����� ���������� ����, ������� ����� ������������ � ��������
			// ��������� ������ setForeground ���� ������-������ resultPanel.
            if (changeNum % 3 == 0) resultPanel.setForeground(Color.cyan);
            else if (changeNum % 3 == 1) resultPanel.setForeground(new Color(50, 205, 50));
            else resultPanel.setForeground(Color.black);
			// ���������� �������� ������� �� ������� �� 2 �������� ���� changeNum.
			// ���������� ����, ������� ����� ������������ � �������� ���������
			// ������ setBackground. ���� ������ ����� �������� ������ ��� ������.
            if(changeNum % 2 == 0) resultPanel.setBackground(new Color(104, 88, 243));
            else resultPanel.setBackground(new Color(64, 64, 64));
			// �������� ������� ���� transition, ������� �������� �� true.
            transition = true;
			// � ������������������ ����� ���� ����� ������ ����� notifyAll,
			// ������� ��������� ��������� ������ � ���, ��� ��� ����� ����� ��
			// ��������� �������� (���� ������� �������) � ���������� ������.
			// ��� � � ������ � ������� wait, ������� ���� � ������� notify/notifyAll
			// ������ ���� ����������� ������������������!
            synchronized (this){
                notifyAll();
            }
        }
		// �������� ���� rotateValue �������������/�����������
		// �� �� ����� ������� �������� � ��������� ������.
        rotateValue += incr / 375.0;
		// �������� ���� perText ����������� � 1/-1 � ����������� �� 
		// ���������� �������� �������� ������ � ���� incr.
        perText += incr;
    }

    private void moveString2(){
		// ���� ���� transition = false, 
		// ����� ��������� � ��������� ��������.
        while (!transition){
            try{
                synchronized (this){
                    wait();
                }
            } catch (InterruptedException e) {}
        }
		// �������� ���������� ����-�������� backWhere100 -
		// ���������� �������� �� 1.
        backWhere100++;
		// � ������, ���� backWhere100 ������ 50, �����������
		// �������� ���������� �������� ���� incr_ot_0_do_50 -
		// ���������� �������� �� 1,
		if(backWhere100 > 50) incr_ot_0_do_50--;
		// ����� ���������� �������� ����������.
		else incr_ot_0_do_50++;
		//��� ���������� �������� backWhere100 �����,
        if(backWhere100 == 100){
			// ����-������ ����������,
            backWhere100 = 0;
			// transition ������������� false,
            transition = false;
			// �������� ���� downFlag �������� �� ���������������.
			downFlag = !downFlag;
			// ����� ������ notifyAll � ������������������
			// ����� ����.
            synchronized (this){
                notifyAll();
            }
        }
    }
	// ����� ��� ������� �������.
	// ���������� �������� ���������� �������
	// Thread, � ������� ��������� �������� this -
	// �� ���� ��������� �������� ������ � 
	// new twoMove(), ���������� �����, � �������
	// ���������� ��������� Runnable, � �����
	// ������������� ����� curThread1 � curThread2
	// ����� ������������. ����� � ������� ���� 
	// ���������� ����� start, �������, � ����
	// �������, �������� ����� run() - ��������� �����
	// �� ����������.
    public void startThreads(){
        curThread1 = new Thread(this);
		curThread1.start();
        curThread2 = new Thread(new twoMove());
        curThread2.start();
    }
	// ����� ��� ��������� �������, ����������
	// ����� curThread1 � curThread2 ������� ������.
    public void stopThreads(){
        curThread1 = null;
        curThread2 = null;
    }
	// ���������� ����� twoMove, ����������� ��������� Runnable.
    class twoMove implements Runnable{
		// ���������� ������ run ���������� Runnable.
        public void run() {
			// ��������� ������ �� ������� �����
            Thread curThread = Thread.currentThread();
			// ���� ������ ����� �������, ���������� �����
			// ������ ����������� ������ ������ � ������ moveString2,
			// ���������� �� 2 ����� ��������.
            while(curThread2 == curThread){
                resultPanel.repaint();
                moveString2();
                try{
                    Thread.sleep(29);
                } catch (InterruptedException e) {}
            }
        }
    }

    public static void main(String args[]){
        Kursovaya canvas = new Kursovaya();
        Frame frame = new Frame("�������� ������. ����� 2");
        frame.add(canvas);
        frame.setSize(800, 700);
        frame.setLocation(100, 100);
        frame.setVisible(true);
        canvas.startThreads();
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
}
