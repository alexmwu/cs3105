import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by aw246 on 12/04/15.
 */
public class VendingStateMachine {
    public static enum inputs{TENP,TWENTYP,FIFTYP,APPLE_BUTTON,BANANA_BUTTON,CHOCOLATE_BUTTON}
    public static enum outputs{NOTHING,APPLE_0P,APPLE_10P,APPLE_20P,APPLE_30P,APPLE_40P,
        APPLE_50P,APPLE_60P,APPLE_70P,BANANA_0P,BANANA_10P,BANANA_20P,BANANA_30P,BANANA_40P,BANANA_50P,BANANA_60P,CHOCOLATE_0P,CHOCOLATE_10P,CHOCOLATE_20P,CHOCOLATE_30P,CHOCOLATE_40P}

    public static enum states{EMPTY,TEN,TWENTY,THIRTY,FORTY,FIFTY,SIXTY,SEVENTY,EIGHTY,NINETY}

    public static void getDFSTour(){
        Stack<Path> tour=new Stack<Path>();

        //add all starting paths
        for(inputs i:inputs.values()){
            tour.push(new Path(states.EMPTY,i));
        }

        while(!tour.isEmpty()){
            Path p=tour.peek();
            System.out.println(p+" "+tour.size());
            p.nextState();
            if(p.currentState==states.EMPTY){
                System.out.println(tour.size());
                tour.pop();
                continue;
            }
            for(inputs i:inputs.values()){
                tour.push(p.copy(p.currentState,i));
            }
        }

    }

    public static void getEulerCycle(){

    }


    public static class Transition{
        inputs in;
        outputs out;
        Transition(inputs in,outputs out){
            this.in=in;
            this.out=out;
        }

        public String toString(){
            return in+", "+out;
        }
    }

    public static class Path {
        ArrayList<inputs> pathInputs;
        ArrayList<outputs> pathOutputs;
        ArrayList<Transition> transitions;
        states currentState;
        inputs nextInput;

        Path(states s,inputs i) {
            pathInputs = new ArrayList<inputs>();
            pathOutputs = new ArrayList<outputs>();
            transitions=new ArrayList<Transition>();
            currentState = s;
            nextInput=i;
        }

        public Path copy(states state,inputs nextI){
            Path p=new Path(currentState,nextI);
            p.pathInputs=this.pathInputs;
            p.pathOutputs=this.pathOutputs;
            return p;
        }

        public void nextState(){
            outputs out=input(nextInput);
            pathInputs.add(nextInput);
            pathOutputs.add(out);
//            transitions.add(new Transition(nextInput,out));
        }

        public String toString(){
            return currentState+", next input: "+nextInput;//+", last input: "+pathInputs.get(pathInputs.size()-1)+", last output: "+pathOutputs.get(pathOutputs.size()-1);
        }

        public outputs input(inputs input){
            switch(currentState){
                case EMPTY:
                    switch(input){
                        case TENP:
                            currentState=states.TEN;
                            return outputs.NOTHING;
                        case TWENTYP:
                            currentState=states.TWENTY;
                            return outputs.NOTHING;
                         case FIFTYP:
                             currentState=states.FIFTY;
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             return outputs.NOTHING;
                         case BANANA_BUTTON:
                             return outputs.NOTHING;
                         case CHOCOLATE_BUTTON:
                             return outputs.NOTHING;
                    }

                    break;
                case TEN:
                    switch(input){
                        case TENP:
                            currentState=states.TWENTY;
                            return outputs.NOTHING;
                        case TWENTYP:
                            currentState=states.THIRTY;
                            return outputs.NOTHING;
                         case FIFTYP:
                             currentState=states.SIXTY;
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             return outputs.NOTHING;
                         case BANANA_BUTTON:
                             return outputs.NOTHING;
                         case CHOCOLATE_BUTTON:
                             return outputs.NOTHING;
                    }

                    break;
                 case TWENTY:
                     switch(input){
                        case TENP:
                            currentState=states.THIRTY;
                            return outputs.NOTHING;
                        case TWENTYP:
                            currentState=states.FORTY;
                            return outputs.NOTHING;
                         case FIFTYP:
                             currentState=states.SEVENTY;
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_0P;
                         case BANANA_BUTTON:
                             return outputs.NOTHING;
                         case CHOCOLATE_BUTTON:
                             return outputs.NOTHING;
                    }

                    break;
                 case THIRTY:
                      switch(input){
                        case TENP:
                            currentState=states.FORTY;
                            return outputs.NOTHING;
                        case TWENTYP:
                            currentState=states.FIFTY;
                            return outputs.NOTHING;
                         case FIFTYP:
                             currentState=states.EIGHTY;
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_10P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_0P;
                         case CHOCOLATE_BUTTON:
                             return outputs.NOTHING;
                    }

                    break;
                 case FORTY:
                      switch(input){
                        case TENP:
                            currentState=states.FIFTY;
                            return outputs.NOTHING;
                        case TWENTYP:
                            currentState=states.SIXTY;
                            return outputs.NOTHING;
                         case FIFTYP:
                             currentState=states.NINETY;
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_20P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_10P;
                         case CHOCOLATE_BUTTON:
                             return outputs.NOTHING;
                    }

                    break;
                 case FIFTY:
                      switch(input){
                        case TENP:
                            return outputs.NOTHING;
                        case TWENTYP:
                            return outputs.NOTHING;
                         case FIFTYP:
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_30P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_20P;
                         case CHOCOLATE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.CHOCOLATE_0P;
                    }

                    break;
                 case SIXTY:
                      switch(input){
                        case TENP:
                            return outputs.NOTHING;
                        case TWENTYP:
                            return outputs.NOTHING;
                         case FIFTYP:
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_40P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_30P;
                         case CHOCOLATE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.CHOCOLATE_10P;
                    }

                    break;
                 case SEVENTY:
                      switch(input){
                        case TENP:
                            return outputs.NOTHING;
                        case TWENTYP:
                            return outputs.NOTHING;
                         case FIFTYP:
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_50P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_40P;
                         case CHOCOLATE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.CHOCOLATE_20P;
                    }

                    break;
                 case EIGHTY:
                      switch(input){
                        case TENP:
                            return outputs.NOTHING;
                        case TWENTYP:
                            return outputs.NOTHING;
                         case FIFTYP:
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_60P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_50P;
                         case CHOCOLATE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.CHOCOLATE_30P;
                    }

                    break;
                 case NINETY:
                      switch(input){
                        case TENP:
                            return outputs.NOTHING;
                        case TWENTYP:
                            return outputs.NOTHING;
                         case FIFTYP:
                             return outputs.NOTHING;
                         case APPLE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.APPLE_70P;
                         case BANANA_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.BANANA_60P;
                         case CHOCOLATE_BUTTON:
                             currentState=states.EMPTY;
                             return outputs.CHOCOLATE_40P;
                    }

                    break;
            }
            return null;
        }
   }


    public static void main(String[] args){
        VendingStateMachine.getDFSTour();
    }
}
