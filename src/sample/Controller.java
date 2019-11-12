package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private LineChart<Number, Number> local_error_graph;
    @FXML
    private LineChart<Number, Number> global_error_graph;
    @FXML
    javafx.scene.control.TextField y0_value;
    @FXML
    javafx.scene.control.TextField x0_value;
    @FXML
    javafx.scene.control.TextField X_value;
    @FXML
    javafx.scene.control.TextField N_value;
    @FXML
    javafx.scene.control.TextField N0_value;
    @FXML
    javafx.scene.control.TextField funct_str;
    @FXML
    javafx.scene.control.TextField deriv_str;
    @FXML
    private javafx.scene.control.CheckBox exact_on;
    @FXML
    private javafx.scene.control.CheckBox euler_on;
    @FXML
    private javafx.scene.control.CheckBox heun_on;
    @FXML
    private javafx.scene.control.CheckBox rungelutta_on;

    private LinkedList<NumericalSolution> solutions = new LinkedList<>();
    private ExactSolution exactSolution;
    private HashMap<String, Boolean> checkboxes = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Get exact solution and derivative
        String fstr = funct_str.getText();
        String dstr = deriv_str.getText();

        // Add the desired plots and checkboxes to
        this.exactSolution = new ExactSolution("Exact", fstr, 1000);
        this.checkboxes.put("Exact", Boolean.TRUE);

        NumericalSolution euler = new EulerMethod("Euler", dstr);
        this.solutions.add(euler);
        this.checkboxes.put("Euler", Boolean.TRUE);

        NumericalSolution heun = new HeunMethod("Heun", dstr);
        this.solutions.add(heun);
        this.checkboxes.put("Heun", Boolean.TRUE);

        NumericalSolution rk = new RungeKuttaMethod("RungeKutta", dstr);
        this.solutions.add(rk);
        this.checkboxes.put("RungeKutta", Boolean.TRUE);
    }

    public void plotBtn()
    {
        // Collect data from text boxes
        float y0 = Float.valueOf(y0_value.getText());
        float x0 = Float.valueOf(x0_value.getText());
        int X = Integer.valueOf(X_value.getText());
        int N0 = Integer.valueOf(N0_value.getText());
        int N = Integer.valueOf(N_value.getText());

        // Collect data from checkboxes
        this.checkboxes.put("Exact", exact_on.isSelected());
        this.checkboxes.put("Euler", euler_on.isSelected());
        this.checkboxes.put("Heun", heun_on.isSelected());
        this.checkboxes.put("RungeKutta", rungelutta_on.isSelected());

        // Collect functions from text boxes
        String fstr = funct_str.getText();
        String dstr = deriv_str.getText();

        // Update Exact NumericalSolution
        this.exactSolution.setFunction(fstr);
        this.exactSolution.updateValues(x0, y0, X);

        // Update Numerical solutions
        if (this.solutions != null)
        {
            for(NumericalSolution s: this.solutions)
            {
               s.setFunction(dstr);
               s.updateValues(x0, y0, X, N);
               s.calculateGlobalError(fstr, x0, y0);
               s.calculateLocalError();
            }
        }

        // Prepare line chart for plots
        graph.getData().clear();
        graph.setCreateSymbols(false);

        // Prepare local error chart
        local_error_graph.getData().clear();
        local_error_graph.setCreateSymbols(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> error_series;

        series.setName(this.exactSolution.getName());
        double[] x = this.exactSolution.getX();
        double[] y = this.exactSolution.getY();
        double[] err;

        // Plot exact solution
        for (int i = 0; i < x.length; i++)
        {
            series.getData().add(new XYChart.Data<>(x[i], y[i]));
        }

        // If checkbox is checked
        if(this.checkboxes.get(series.getName())) {
            graph.getData().add(series);
        }

        // Plot numerical solutions
        if (this.solutions != null)
        {
            for(NumericalSolution s: this.solutions)
            {
                // If checkbox is checked
                if(!this.checkboxes.get(s.getName()))
                {
                    continue;
                }

                series = new XYChart.Series<>();
                error_series = new XYChart.Series<>();
                series.setName(s.getName());
                error_series.setName(s.getName());
                x = s.getX();
                y = s.getY();
                err = s.getLocalError();

                for (int i = 0; i < x.length; i++)
                {
                    series.getData().add(new XYChart.Data<>(x[i], y[i]));
                    error_series.getData().add(new XYChart.Data<>(x[i], err[i]));
                }

                graph.getData().add(series);
                local_error_graph.getData().add(error_series);
            }
        }

        // Prepare global error chart
        global_error_graph.getData().clear();
        global_error_graph.setCreateSymbols(false);
        XYChart.Series<Number, Number> global_error_series;

        // Analyze global errors
        if (this.solutions != null)
        {
            for(NumericalSolution s: this.solutions)
            {
                // If checkbox is checked
                if(!this.checkboxes.get(s.getName()))
                {
                    continue;
                }

                global_error_series = new XYChart.Series<>();
                global_error_series.setName(s.getName());
                s.setFunction(dstr);

                // Go through every grid size
                for (int i = N0; i <= N; i++)
                {
                    s.updateValues(x0, y0, X, i);
                    s.calculateGlobalError(fstr, x0, y0);
                    double ge[] = s.getGlobalError();
                    global_error_series.getData().add(new XYChart.Data<>(i, ge[ge.length-1]));
                }
                global_error_graph.getData().add(global_error_series);
            }
        }
    }
}
