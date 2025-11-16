import java.util.HashMap;
import java.util.Map;

public class Atom {
    Map<String, Integer> O = new HashMap<>();
    Map<String, Integer> C = new HashMap<>();
    Map<String, Integer> H = new HashMap<>();

    Bond bond_manager; // убрать инициализацию здесь
    private int O_count = 0;
    private int C_count = 0;
    private int H_count = 0;

    // Конструктор, принимающий Bond
    public Atom(Bond bondManager) {
        this.bond_manager = bondManager;
    }

    // Конструктор по умолчанию (без Bond)
    public Atom() {
        // bond_manager будет установлен позже через setter
    }

    // Сеттер для bond_manager
    public void setBondManager(Bond bondManager) {
        this.bond_manager = bondManager;
    }

    public void create_O() {
        O.put("O_" + O_count, 0);
        O_count += 1;
        System.out.println("created O_" + O_count);
    }

    public void create_C() {
        C.put("C_" + C_count, 0);
        C_count += 1;
        System.out.println("created C_" + O_count);
    }

    public void create_H() {
        H.put("H_" + H_count, 0);
        H_count += 1;
        System.out.println("created H_" + O_count);
    }

    public void delete_O(String O_num) {
        if (bond_manager != null) {
            bond_manager.delete_all_bonds(O_num);
        }
        O.remove(O_num);
    }

    public void delete_C(String C_num) {
        if (bond_manager != null) {
            bond_manager.delete_all_bonds(C_num);
        }
        C.remove(C_num);
    }

    public void delete_H(String H_num) {
        if (bond_manager != null) {
            bond_manager.delete_all_bonds(H_num);
        }
        H.remove(H_num);
    }

    public void delete_bound_O(String O_num) {
        if (O.containsKey(O_num)) {
            O.put(O_num, O.get(O_num) - 1);
        }
    }

    public void delete_bound_C(String C_num) {
        if (C.containsKey(C_num)) {
            C.put(C_num, C.get(C_num) - 1);
        }
    }

    public void delete_bound_H(String H_num) {
        if (H.containsKey(H_num)) {
            H.put(H_num, H.get(H_num) - 1);
        }
    }
}