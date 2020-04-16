import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception  {
        Scanner scanner = new Scanner(new File("input.txt"));
        double[] xArr = new double[50];
        double[] yArr = new double[50];
        double[] xyArr = new double[100];
        int n = 0;

        while (scanner.hasNextDouble()) {
            xyArr[n] = scanner.nextDouble();
            n++;
        }
        for (int i = 0; i < n / 2; i++) { //собираем значения из файла
            xArr[i] = xyArr[i];
            yArr[i] = xyArr[i + n / 2];

        }
        String minName = "Линейная";

        FileWriter writer = new FileWriter("output.txt", false);
        // запись всей строки
        String text = "Вид функции         |    a    |    b    |    c    |    S    |  Sigma  |";
        writer.write(text);
        writer.append('\n');

        double [] ansLine = Solver.linearApprox(xArr, yArr, n / 2);
        double min = ansLine[3];
        text = "f = a*x + b         |  " + String.format("%.3f", ansLine[0]) + "  |  " + String.format("%.3f", ansLine[1])
                + "  |    -    |  " + String.format("%.3f", ansLine[2])
                + "  |  " + String.format("%.3f", ansLine[3]) + "  |";
        writer.write(text);
        writer.append('\n');
        writer.flush();

        double[] ansQuad = Solver.quadraticApprox(xArr, yArr, n / 2);
        text = "f = a*x^2 + b*x + c |  " + String.format("%.3f", ansQuad[0]) + "  |  " + String.format("%.3f", ansQuad[1])
                + "  |  " + String.format("%.3f", ansQuad[2]) +"  |  " + String.format("%.3f", ansQuad[3])
                + "  |  " + String.format("%.3f", ansQuad[4]) + "  |";
        writer.write(text);
        writer.append('\n');
        writer.flush();
        if(min > ansQuad[4]) {
            min = ansQuad[4];
            minName = "Квадратичная";
        }

        double[] ansPow = Solver.powApprox(xArr, yArr, n / 2);
        text = "f = a*x^b           |  " + String.format("%.3f", ansPow[0]) + "  |  " + String.format("%.3f", ansPow[1])
                + "  |    -    |  " + String.format("%.3f", ansPow[2])
                + "  |  " + String.format("%.3f", ansPow[3]) + "  |";
        writer.write(text);
        writer.append('\n');
        writer.flush();
        if(min > ansPow[3]) {
            min = ansPow[3];
            minName = "Степенная";
        }

        double[] ansExp = Solver.expApprox(xArr, yArr, n / 2);
        text = "f = a*e^(bx)        |  " + String.format("%.3f", ansExp[0]) + "  |  " + String.format("%.3f", ansExp[1])
                + "  |    -    |  " + String.format("%.3f", ansExp[2])
                + "  |  " + String.format("%.3f", ansExp[3]) + "  |";
        writer.write(text);
        writer.append('\n');
        writer.flush();
        if(min > ansExp[3]) {
            min = ansExp[3];
            minName = "Эксопоненциальная";
        }

        double [] ansLog = Solver.logApprox(xArr, yArr, n / 2);
        text = "f = a*lnx + b       |  " + String.format("%.3f", ansLog[0]) + "  |  " + String.format("%.3f", ansLog[1])
                + "  |    -    |  " + String.format("%.3f", ansLog[2])
                + "  |  " + String.format("%.3f", ansLog[3]) + "  |";
        writer.write(text);
        writer.append('\n');
        writer.flush();
        if(min > ansExp[3]) {
            min = ansExp[3];
            minName = "Логарифмическая";
        }
        writer.write("Наилучшая аппроксимирующая функция - " + minName + "!");
        writer.append('\n');
        writer.flush();
        writer.close();



        //вывод графика
        stage.setTitle("График");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);

        XYChart.Series seriesLine = new XYChart.Series();
        XYChart.Series seriesQuad = new XYChart.Series();
        XYChart.Series seriesPow = new XYChart.Series();
        XYChart.Series seriesExp = new XYChart.Series();
        XYChart.Series seriesLog = new XYChart.Series();
        XYChart.Series seriesInput = new XYChart.Series();
        seriesLine.setName("Линейный");
        seriesQuad.setName("Квадратичный");
        seriesPow.setName("Степенной");
        seriesExp.setName("Экспонента");
        seriesLog.setName("Логарифмический");
        seriesInput.setName("Искомый");


        for (int i = 0; i < n/2; i++) {
            seriesLine.getData().add(new XYChart.Data(xArr[i], ansLine[0]*xArr[i] +ansLine[1]));
            seriesQuad.getData().add(new XYChart.Data(xArr[i], ansQuad[0]*xArr[i]*xArr[i] +ansLine[1]*xArr[i] + ansLine[2]));
            seriesPow.getData().add(new XYChart.Data(xArr[i], Math.pow((ansPow[0] * Math.pow(xArr[i], ansPow[1]) - yArr[i]), 2)));
            seriesExp.getData().add(new XYChart.Data(xArr[i], ansLine[0]*Math.pow(Math.E, xArr[i]*ansLine[1])));
            seriesLog.getData().add(new XYChart.Data(xArr[i], ansLine[0]*Math.log(xArr[i]) +ansLine[1]));
            seriesInput.getData().add(new XYChart.Data(xArr[i], yArr[i]));
        }

        Scene scene = new Scene(lineChart, 1200, 900);
        lineChart.getData().add(seriesLine);
        lineChart.getData().add(seriesQuad);
        lineChart.getData().add(seriesPow);
        lineChart.getData().add(seriesExp);
        lineChart.getData().add(seriesLog);
        lineChart.getData().add(seriesInput);

        stage.setScene(scene);
        stage.show();



    }

}