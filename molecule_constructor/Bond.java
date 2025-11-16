import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Bond {
    Map<String, String> bonds = new HashMap<>();
    private int bond_count = 0;
    Atom atom_manager; // убрать инициализацию здесь

    // Конструктор, принимающий Atom
    public Bond(Atom atomManager) {
        this.atom_manager = atomManager;
    }

    public void create_bond(String atom1, String atom2) {
        bonds.put("bond_" + bond_count, atom1 + " " + atom2);
        bond_count += 1;
    }

    public void delete_all_bonds(String atom) {
        // Создаем копию ключей для безопасного удаления
        String[] keys = bonds.keySet().toArray(new String[0]);
        for (String key : keys) {
            String[] bond = bonds.get(key).split(" ");
            if (Objects.equals(atom, bond[0]) || Objects.equals(atom, bond[1])) {
                // Извлекаем номер из ключа "bond_X"
                int bondNum = Integer.parseInt(key.split("_")[1]);
                delete_bond(bondNum);
            }
        }
    }

    public void delete_bond(int bond_num) {
        String bondKey = "bond_" + bond_num;
        if (bonds.containsKey(bondKey)) {
            String[] bond = bonds.get(bondKey).split(" ");
            for (int i = 0; i < 2; i++) {
                switch (bond[i].charAt(0)) {
                    case 'O':
                        atom_manager.delete_bound_O(bond[i]);
                        break; // добавить break!
                    case 'C':
                        atom_manager.delete_bound_C(bond[i]);
                        break; // добавить break!
                    case 'H':
                        atom_manager.delete_bound_H(bond[i]);
                        break; // добавить break!
                }
            }
            bonds.remove(bondKey);
        }
    }
}