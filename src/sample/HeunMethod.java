package sample;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

class HeunMethod extends NumericalSolution
{
    HeunMethod(String name, String function)
    {
        this.setName(name);
        this.setFunction(function);
    }

    @Override
    void updateValues(double x0, double y0, double X, int N)
    {
        double[] x = new double[N], y = new double[N];

        // Initial values
        x[0] = x0;
        y[0] = y0;

        double dx = (X-x0) / (N-1);

        Expression exp = new ExpressionBuilder(this.getFunction()).variables("x", "y").build();


        for (int i = 1; i < N; i++)
        {
            x[i] = x[i-1] + dx;

            exp.setVariable("x", x[i-1]);
            exp.setVariable("y", y[i-1]);
            double k1 = exp.evaluate();
            exp.setVariable("x", x[i-1] + dx);
            exp.setVariable("y", y[i-1] + dx * k1);
            double k2 = exp.evaluate();
            y[i] = y[i-1] + dx / 2 * (k1 + k2);
        }

        this.setX(x);
        this.setY(y);
    }
}
