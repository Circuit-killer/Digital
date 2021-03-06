package de.neemann.digital.builder;

import de.neemann.digital.analyse.expression.Expression;
import de.neemann.digital.analyse.expression.Variable;
import de.neemann.digital.core.element.PinDescription;
import de.neemann.digital.lang.Lang;

import java.util.*;

/**
 * A PinMap.
 * Used to assign a symbolic name to a pin number
 *
 * @author hneemann
 */
public class PinMap {
    private final HashMap<String, Integer> pinMap;
    private final ArrayList<Pin> availPins;
    private ArrayList<HashSet<String>> alias;

    /**
     * Creates a new instance
     */
    public PinMap() {
        pinMap = new HashMap<>();
        alias = new ArrayList<>();
        availPins = new ArrayList<>();
    }

    /**
     * Sets the available input pin numbers
     *
     * @param inputPins the input pins
     * @return this for chained calls
     */
    public PinMap setAvailInputs(int... inputPins) {
        for (int p : inputPins)
            availPins.add(new Pin(p, PinDescription.Direction.input));
        return this;
    }

    /**
     * Sets the available output pin numbers
     *
     * @param outputPins the input pins
     * @return this for chained calls
     */
    public PinMap setAvailOutputs(int... outputPins) {
        for (int p : outputPins)
            availPins.add(new Pin(p, PinDescription.Direction.output));
        return this;
    }

    /**
     * Sets the available bidirectional pin numbers
     *
     * @param outputPins the input pins
     * @return this for chained calls
     */
    public PinMap setAvailBidirectional(int... outputPins) {
        for (int p : outputPins)
            availPins.add(new Pin(p, PinDescription.Direction.both));
        return this;
    }

    /**
     * Assign a symbolic name to a pin
     *
     * @param name the name
     * @param pin  the pin
     * @return this for chained calls
     * @throws PinMapException FuseMapFillerException
     */
    public PinMap assignPin(String name, int pin) throws PinMapException {
        if (name == null || name.length() == 0)
            throw new PinMapException(Lang.get("err_pinMap_NoNameForPin_N", pin));
        if (pinMap.containsKey(name))
            throw new PinMapException(Lang.get("err_pinMap_Pin_N_AssignedTwicePin", name));
        if (pinMap.containsValue(pin))
            throw new PinMapException(Lang.get("err_pinMap_Pin_N_AssignedTwicePin", pin));

        pinMap.put(name, pin);
        return this;
    }

    /**
     * Assigns pins to names.
     * Strings must have a form of "a=5, Q_0=6"
     *
     * @param assignment the assignment string
     * @return this for chained calls
     * @throws PinMapException PinMapException
     */
    public PinMap parseString(String assignment) throws PinMapException {
        if (assignment == null)
            return this;

        StringTokenizer st = new StringTokenizer(assignment, ";,");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            int p = tok.indexOf("=");
            if (p < 0) throw new PinMapException(Lang.get("err_pinMap_noEqualsfound"));

            String name = tok.substring(0, p).trim();
            String numStr = tok.substring(p + 1).trim();
            try {
                int num = Integer.parseInt(numStr);
                assignPin(name, num);
            } catch (NumberFormatException e) {
                throw new PinMapException(e);
            }
        }
        return this;
    }

    /**
     * returns true id the expression is a simple variable
     * Checks if the assignment is a simple A=B. If true an alias for A is generated in the pin map.
     * This is needed to void to assign two pins to the same logical signal.
     *
     * @param name       the name of the target
     * @param expression the expression to check
     * @return true if expression is a simple variable
     */
    public boolean isSimpleAlias(String name, Expression expression) {
        if (expression instanceof Variable) {
            String al = ((Variable) expression).getIdentifier();

            HashSet<String> found = null;
            for (HashSet<String> s : alias)
                if (s.contains(name) || s.contains(al)) {
                    found = s;
                    break;
                }

            if (found == null) {
                found = new HashSet<>();
                alias.add(found);
            }

            found.add(name);
            found.add(al);

            return true;
        }
        return false;
    }

    /**
     * Adds the given pin assignments to this pin map
     *
     * @param pinMap the given assignments
     * @return this for chained calls
     * @throws PinMapException PinMapException
     */
    public PinMap addAll(Map<String, String> pinMap) throws PinMapException {
        if (pinMap != null)
            for (Map.Entry<String, String> e : pinMap.entrySet()) {
                try {
                    assignPin(e.getKey(), Integer.parseInt(e.getValue()));
                } catch (NumberFormatException ex) {
                    throw new PinMapException(Lang.get("err_pinIsNotANumber_N", e.getValue()));
                }
            }
        return this;
    }

    private Integer searchFirstFreePin(PinDescription.Direction direction, String name) {
        for (Pin pin : availPins) {
            if (!pinMap.containsValue(pin.num))
                if (pin.direction.equals(direction) || pin.direction.equals(PinDescription.Direction.both)) {
                    pinMap.put(name, pin.num);
                    return pin.num;
                }
        }
        return null;
    }

    private boolean isAvailable(PinDescription.Direction direction, int p) {
        for (Pin pin : availPins)
            if (pin.num == p)
                return (pin.direction.equals(direction) || pin.direction.equals(PinDescription.Direction.both));
        return false;
    }

    /**
     * Gets the input pin number for the symbolic name.
     * If no assignment found on of the pins is selected automatically
     *
     * @param in the name
     * @return the  pin number
     * @throws PinMapException PinMap
     */
    public int getInputFor(String in) throws PinMapException {
        return getPinFor(in, PinDescription.Direction.input);
    }

    /**
     * gets the assigned pin.
     *
     * @param in the pins name
     * @return the pin number or -1 if not assigned
     */
    public int isAssigned(String in) {
        Integer p = searchPinWithAlias(in);
        if (p == null) return -1;
        else return p;
    }

    private int getPinFor(String in, PinDescription.Direction direction) throws PinMapException {
        Integer p = searchPinWithAlias(in);
        if (p == null)
            p = searchFirstFreePin(direction, in);
        if (p == null) {
            if (direction.equals(PinDescription.Direction.input))
                throw new PinMapException(Lang.get("err_pinMap_toMannyInputsDefined"));
            else
                throw new PinMapException(Lang.get("err_pinMap_toMannyOutputsDefined"));
        } else if (!isAvailable(direction, p)) {
            if (direction.equals(PinDescription.Direction.input))
                throw new PinMapException(Lang.get("err_pinMap_pin_N0_isNotAnInput", p));
            else
                throw new PinMapException(Lang.get("err_pinMap_pin_N0_isNotAnOutput", p));
        }
        return p;
    }

    private Integer searchPinWithAlias(String pinName) {
        for (HashSet<String> aliasSet : alias)
            if (aliasSet.contains(pinName)) { // the are aliases
                for (String n : aliasSet) {
                    Integer p = pinMap.get(n);
                    if (p != null)
                        return p;
                }
            }
        return pinMap.get(pinName);
    }

    /**
     * Gets the output pin number for the symbolic name.
     * If no assignment is found on of the pins is selected automatically
     *
     * @param out the name
     * @return the  pin number
     * @throws PinMapException FuseMapFillerException
     */
    public int getOutputFor(String out) throws PinMapException {
        return getPinFor(out, PinDescription.Direction.output);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Lang.get("msg_pins")).append(":\n");
        for (Map.Entry<String, Integer> p : pinMap.entrySet())
            sb.append(Lang.get("msg_pinMap_pin_N_is_N", p.getValue(), p.getKey())).append("\n");

        return sb.toString();
    }

    private static final class Pin {
        private final int num;
        private final PinDescription.Direction direction;

        private Pin(int num, PinDescription.Direction direction) {
            this.num = num;
            this.direction = direction;
        }
    }
}
