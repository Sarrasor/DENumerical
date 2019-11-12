package sample;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

abstract class NumericalSolution
{
    private double[] x;
    private double[] y;
    private double[] localError;
    private double[] globalError;
    private String name;
    private String function;

    abstract void updateValues(double x0, double y0, double X, int N);

    String getFunction()
    {
        return function;
    }

    void setFunction(String function)
    {
        this.function = function;
    }

    double[] getX()
    {
        return x;
    }

    void setX(double[] x) {
        this.x = x;
    }

    double[] getY() {
        return y;
    }

    void setY(double[] y) {
        this.y = y;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    double[] getLocalError() {
        return localError;
    }

    double[] getGlobalError() {
        return globalError;
    }


    void calculateGlobalError(String f, double x0, double y0)
    {
        Expression exp = new ExpressionBuilder(f).variables("x", "x0", "y0").build();
        exp.setVariable("x0", x0);
        exp.setVariable("y0", y0);
        double[] global = new double[this.x.length];

        for (int i = 0; i < x.length; i++)
        {
            exp.setVariable("x", x[i]);
            global[i] = exp.evaluate() - this.y[i];
        }

        this.globalError = global;
    }

    void calculateLocalError()
    {
        double[] local = new double[globalError.length];

        for (int i = 1; i < globalError.length; i++)
        {
            local[i] = globalError[i] - globalError[i-1];
        }

        local[0] = local[1];


        this.localError = local;
    }
}
