package ai.connect4;

import sac.StateFunction;
import sac.State;

public class MainHeuristic extends StateFunction {
	@Override
    public double calculate(State state) {
        Connect4 c4 = (Connect4) state;
        if (c4.checkGameBoard())
            return ((c4.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        return c4.boardValue();
    }

}