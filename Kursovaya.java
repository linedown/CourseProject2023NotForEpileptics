/* Выполнил обучающийся Нельин А.А. из группы ИВБ-111. Вариант 27.
	Курсовая работа. Часть 2. Анимация.
	Приложение №2 разрабатывается на основе приложения №1. Анимационный эффект в панели
	для рисования (например, движение графического примитива по определенной траектории)
	может быть выбран произвольно, но должен соответствовать заданной теме рисования
	и отличаться хотя бы некоторой оригинальностью реализации.
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

// Основной класс приложения, добавление интерфейса Runnable
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
	// Объявление полей, которые будут использоваться
	// при реализации эффекта анимации
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
	// Объявление полей типа Thread - потоков, на которых
	// будет реализован эффект анимации и инициализация их нулевыми ссылками
	Thread curThread1 = null;
    Thread curThread2 = null;
    public Kursovaya(){
		// Инициализация полей
		// Поле, используемое при проверке
		// результата остатка деления на 2 и на 3.
		// Необходимо для поочерёдной смены цветов
		// внешнего и заднего фонов.
		changeNum = 0;
		// Поле-инкремент. Используется в 1 части анимации.
		// По её завершении к полю применяется операция унарного
		// минуса.
		incr = 1;
		// Значение, передающееся методу rotate
		// объекта типа Graphics2D. Изменяется в процессе
		// работы потоков.
		rotateValue = 0;
		// Поле, значение которого передаётся при отрисовке строк.
		perText = 0;
		// Поле-счётчик, изменяемое от 0 до 100. Используется
		// во 2 части анимации. Обнуляется по её завершении.
		backWhere100 = 0;
		// Поле типа byte, изменяемое от 0 до 50 и от 50 до 0. Необходимо
		// для плавного перемещения строк во 2 части анимации.
		incr_ot_0_do_50 = 0;
		// Поле, используемое для вызова методов остановки
		// и возобновления работы потоков.
		stopFlag = false;
		// Поле, используемое при проверке перехода с одного потока
		// на другой.
		transition = false;
		// Поле, необходимое при отрисовке отдельных символов вторым
		// потоком.
		downFlag = false;
		
        setLayout(new GridLayout(1, 2));
        changePanel = new Panel(new GridLayout(3, 1));
		
		// Панель-холст на основе анонимного внутреннего класса
        resultPanel = new Canvas(){
			// Рисование с использованием двойной буферизации 
			//(для предотвращения мерцания)
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
			// Метод, отвечающий за рисование строк на панели-холсте
            private void drawStr(){
				int y = 25;
				// получение строкового литерала с помощью метода 
				// getText у поля textLine типа TextField
                String textStr = textLine.getText();
                if("Italic".equals(styleStr)) fontNum = Font.ITALIC;
                else if("Plain".equals(styleStr)) fontNum = Font.PLAIN;
                else fontNum = Font.BOLD;
                g2d.setFont(new Font(fontStr, fontNum, (sizeSb.getValue() + 15) / 2));
				// Отрисовка строк-подсказок в правом верхнем углу экрана.
				g2d.drawString("Нажмите на", getSize().width - 135, y);
				g2d.drawString("кнопку мыши", getSize().width - 135, y += 20);
				g2d.drawString("для останова или", getSize().width - 135, y += 20);
				g2d.drawString("возобновления", getSize().width - 135, y += 20);
				g2d.drawString("потоков.", getSize().width - 135, y += 20);
				// Вращение текстовой строки на угол rotateValue (в радианах) относительно
				// точки центра панели-холста смещенного на 20 по обеим осям.
                g2d.rotate(rotateValue, (getSize().width - 20) / 2, (getSize().height - 20) / 2);
                if(!transition){
					// Если transition = false, выполняется отрисовка для 1 части анимации -
					// отрисовка целой строки, с постепенно изменяемыми координатами x и y.
					// Также угол вращения изменяется благодаря изменению значения поля rotateValue.
                    g2d.drawString(textStr, 5 + perText, 20 + perText);
                } else{
					// Для синхронного переноса и эффекта дрожания раскомментировать
					// эту часть кода и закомментировать соответствующую в циклах for ниже
					/*g2d.translate(Math.random() * 2, Math.random() * 2);
					g2d.translate(incr_ot_0_do_50 / 2.5, 0);*/
					// Иначе происходит посимвольная отрисовка полученного строкого литерала.
					// Причём: 
					if(downFlag){
						// Если downFlag = true, то происходит отрисовка снизу вверх - то есть
						// чем больше индекс у символа, тем выше он расположен.
						// Расположение диагональное.
						for(int i = 0; i < textStr.length(); i++){
							// Отдельно перед отрисовкой каждого символа вызывается метод
							// translate, куда передаются параметры с помощью метода 
							// random класса Math. Данный метод генерирует и возвращает
							// псевдослучайное число с плавающей запятой в диапазоне [0, 1).
							// При умножении на 2 будет получено число в диапазоне [0, 2).
							// Благодаря тому, что каждый раз при отрисовке генерируются
							// различные числовые значения у каждого символа, достигается
							// эффект дрожания символов. Его реализует перемещение
							// на псевдослучайное число на неслишком большой диапазон от
							// координат отрисовки строк.
							g2d.translate(Math.random() * 2, Math.random() * 2);
							// Плавное перемещение строк-символов вправо-влево, благодаря
							// медленно изменяющемуся полю-счётчику incr_ot_0_do_50
							g2d.translate(incr_ot_0_do_50 / 2.5, 0);
							// Собственно, метод отрисовки символов в виде строки. Так как 1 аргумент
							// должен быть типом String, нужно произвести конкатенацию полученного
							// символа по индексу (с помощью метода charAt) с пустой строкой.
							// Это будет считаться неявным приведением типа char к типу String.
							//g2d.drawString("" + textStr.charAt(i), 5 + perText + 15 * i, 20 + perText + 10 * i);
							g2d.drawString("" + textStr.charAt(i), 5 + perText + 15 * i, 20 + perText + (getWidth() * incr_ot_0_do_50 * i) / (2 * getHeight()));
						}
                    } else{
						// Если downFlag = true, то происходит отрисовка сверху вниз - то есть
						// чем больше индекс у символа, тем ниже он расположен.
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
		// Добавление приёмника события от мыши для панели-холста
		resultPanel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				stopFlag = !stopFlag;
				// При нажатии на ЛКМ/ПКМ будет происходит вызов 
				// методов stopThreads и startThreads в зависимости от того,
				// в каком состоянии находится значение поля stopFlag типа
				// boolean.
				// При остановке потоков вызывается метод stopThreads,
				// в котором полям curThread1 и curThread2 типа Thread
				// будут присвоены нулевые ссылки (null)
				if(stopFlag) stopThreads();
				// Обратный процесс - запуск потоков, подразумевающий под собой
				// вызов метода startThreads, в котором происходит создание 
				// экземпляров классов Thread и инициализация полей curThread1 и curThread2
				// созданными экземплярами.
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
        colorTextList.add("черный");
        colorTextList.add("серый");
        colorTextList.add("синий");
        colorTextList.add("красный");
        colorTextList.add("зеленый");
        colorTextList.add("оранжевый");
        colorTextList.add("желтый");
        colorTextList.add("белый");
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
        colorCb1 = new Checkbox("Зеленый", colorCbg, false);
        colorCb2 = new Checkbox("Бордовый", colorCbg, false);
        colorCb3 = new Checkbox("Синий", colorCbg, false);
        colorCb4 = new Checkbox("Голубой", colorCbg, true);
        colorCb1.addItemListener(this);
        colorCb1.setEnabled(false);
        colorCb2.addItemListener(this);
        colorCb2.setEnabled(false);
        colorCb3.addItemListener(this);
        colorCb3.setEnabled(false);
        colorCb4.addItemListener(this);
        colorCb4.setEnabled(false);

        sizeLabel = new Label("Размеры", Label.CENTER);
        sizeSb = new Scrollbar(Scrollbar.HORIZONTAL, 15, 1, 1, 90);
        sizeSb.setEnabled(false);
        translateVertLabel = new Label("Переместить по вертикали", Label.CENTER);
        translateVertSb = new Scrollbar(Scrollbar.HORIZONTAL, 135, 1, 1, 300);
        translateVertSb.setEnabled(false);
        translateHorLabel = new Label("Переместить по горизонтали", Label.CENTER);
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
            if("Голубой".equals(s)) resultPanel.setBackground(new Color(104, 88, 243));
            else if("Синий".equals(s)) resultPanel.setBackground(new Color(0, 0, 153));
            else if("Бордовый".equals(s)) resultPanel.setBackground(new Color(144, 0, 0));
            else if("Зеленый".equals(s)) resultPanel.setBackground(new Color(0, 153, 0));
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
	// Реализация метода run интерфейса Runnable.
    public void run(){
		// Получение ссылки на текущий поток.
        Thread curThread = Thread.currentThread();
		// Пока данный поток текущий, происходит вызов
		// метода перерисовки панели холста и метода moveString1,
		// отвечающий за 1 часть анимации.
        while(curThread1 == curThread) {
            resultPanel.repaint();
            moveString1();
            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {}
        }
    }
	// Методы moveString1 и moveString2 вызывают активные потоки 
	// для реализации задуманной анимации с текстовой строкой,
	// отрисованной на панель-холст resultPanel.
	
	// Метод moveString1
    private void moveString1() {
		// Пока поле transition = true, происходит вызов метода wait -
		// то есть поток будет находиться в состоянии ожидания.
		// Участок кода, где вызывается метод wait обязательно должен
		// быть синхронизированным (с помощью оператора synchronized),
		// иначе могут возникнуть неожиданные последствия для потоков.
		// В обязательном порядке нужно обработать исключение
		// InterruptedException. Это можно сделать и таким способом,
		// описанным ниже в блоке catch.
        while (transition){
            try{
                synchronized (this){
                    wait();
                }
            } catch (InterruptedException e) {}
        }
		// Максимальное значение, которому может равняться поле perText, определяется исходя
		// из соотношения ширины и высоты панели-холста. Выбирается наименьшее из этих значений.
        sizeMax = (resultPanel.getWidth() >= resultPanel.getHeight()) ? resultPanel.getHeight() - 60 : resultPanel.getWidth() - 60;
		// Если значение поля perText стало меньше 0 или больше sizeMax,
        if (perText >= sizeMax || perText < 0) {
			// происходит увеличение значение поля changeNum, отвечающего за изменение цвета
			// текста (переднего фона) и заднего фона,
            changeNum++;
			// к incr применяется операция унарного минуса.
            incr = -incr;
			// Происходит проверка остатка от деления на 3 значения поля changeNum.
			// Из этого выбирается цвет, который будет передаваться в качестве
			// аргумента методу setForeground поля панели-холста resultPanel.
            if (changeNum % 3 == 0) resultPanel.setForeground(Color.cyan);
            else if (changeNum % 3 == 1) resultPanel.setForeground(new Color(50, 205, 50));
            else resultPanel.setForeground(Color.black);
			// Происходит проверка остатка от деления на 2 значения поля changeNum.
			// Выбирается цвет, которым будет передаваться в качестве параметра
			// методу setBackground. Этим цветом будет покрашен задний фон холста.
            if(changeNum % 2 == 0) resultPanel.setBackground(new Color(104, 88, 243));
            else resultPanel.setBackground(new Color(64, 64, 64));
			// Значение важного поля transition, изменит значение на true.
            transition = true;
			// В синхронизированном блоке кода будет вызван метод notifyAll,
			// который оповестит остальным потоки о том, что они могут выйти из
			// состояния ожидания (если истинно условие) и продолжить работу.
			// Как и в случае с методом wait, участок кода с методом notify/notifyAll
			// должен быть обязательно синхронизированным!
            synchronized (this){
                notifyAll();
            }
        }
		// Значение поля rotateValue увеличивается/уменьшается
		// на не очень большую величину с плавающей точкой.
        rotateValue += incr / 375.0;
		// Значение поля perText суммируется с 1/-1 в зависимости от 
		// результата операции унарного минуса к полю incr.
        perText += incr;
    }

    private void moveString2(){
		// Пока поле transition = false, 
		// поток находится в состоянии ожидания.
        while (!transition){
            try{
                synchronized (this){
                    wait();
                }
            } catch (InterruptedException e) {}
        }
		// Операция инкремента поля-счётчика backWhere100 -
		// увеличение значения на 1.
        backWhere100++;
		// В случае, если backWhere100 больше 50, выполняется
		// операция декремента значения поля incr_ot_0_do_50 -
		// уменьшение значения на 1,
		if(backWhere100 > 50) incr_ot_0_do_50--;
		// иначе происходит операция инкремента.
		else incr_ot_0_do_50++;
		//При достижении значения backWhere100 сотни,
        if(backWhere100 == 100){
			// Поле-счётик обнуляется,
            backWhere100 = 0;
			// transition присваивается false,
            transition = false;
			// значения поля downFlag меняется на противоположное.
			downFlag = !downFlag;
			// Вызов метода notifyAll в синхронизированном
			// блоке кода.
            synchronized (this){
                notifyAll();
            }
        }
    }
	// Метод для запуска потоков.
	// Происходит создание экзепляров классов
	// Thread, в который передаётся параметр this -
	// то есть экземпляр текущего класса и 
	// new twoMove(), внутренний класс, в котором
	// реализован интерфейс Runnable, а также
	// инициализация полей curThread1 и curThread2
	// этими экземплярами. Далее у каждого поля 
	// вызывается метод start, который, в свою
	// очередь, вызывает метод run() - запускает поток
	// на выполнение.
    public void startThreads(){
        curThread1 = new Thread(this);
		curThread1.start();
        curThread2 = new Thread(new twoMove());
        curThread2.start();
    }
	// Метод для остановки потоков, присвающий
	// полям curThread1 и curThread2 нулевые ссылки.
    public void stopThreads(){
        curThread1 = null;
        curThread2 = null;
    }
	// Внутренний класс twoMove, реализующий интерфейс Runnable.
    class twoMove implements Runnable{
		// Реализация метода run интерфейса Runnable.
        public void run() {
			// Получение ссылки на текущий поток
            Thread curThread = Thread.currentThread();
			// Пока данный поток текущий, происходит вызов
			// метода перерисовки панели холста и метода moveString2,
			// отвечающий за 2 часть анимации.
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
        Frame frame = new Frame("Курсовая работа. Часть 2");
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
