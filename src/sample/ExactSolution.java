package sample;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

class ExactSolution
{
    private double[] x;
    private double[] y;
    private int N;
    private String name;
    private String function;

    ExactSolution(String name, String function, int percision)
    {
        this.name = name;
        this.function = function;
        this.N = percision;
    }

    void updateValues(double x0, double y0, double X)
    {
        double[] x = new double[N], y = new double[N];
        x[0] = x0;
        double dx = (X-x0) / (N-1);

        for (int i = 1; i < N; i++)
        {
            x[i] = x[i-1] + dx;
        }

        Expression exp = new ExpressionBuilder(this.function).variables("x", "x0", "y0").build();
        exp.setVariable("x0", x0);
        exp.setVariable("y0", y0);

        for (int i = 0; i < N; i++)
        {
            exp.setVariable("x", x[i]);
            y[i] = exp.evaluate();
        }

        this.x = x;
        this.y = y;
    }

    void setFunction(String function)
    {
        this.function = function;
    }

    double[] getX()
    {
        return x;
    }

    double[] getY() {
        return y;
    }

    String getName() {
        return name;
    }
}
