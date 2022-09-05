package search;

public class BinarySearchMissing {
    public static void main(String[] args) {
        int[] mas = new int[args.length - 1];
        try {
            for (int i = 0; i < args.length - 1; i++) {
                mas[i] = Integer.parseInt(args[i + 1]);
            }
            System.out.println(recursiveSearch(mas, Integer.parseInt(args[0])));
        } catch (NumberFormatException e) {
            System.out.println("Вы ввели нечисловое значение");
        }
    }
    /*
    Обозначения:
      0) "->" == "Значит"
      1) Sorted -> Для любого i принадлежащего промежутку [0; mas.length - 1]: mas[i] <= mas[i - 1]
      2) in -> Принадлежит промежутку
      3) V(i) -> Для любого i
      4) Z -> Все целые числа
      5) minInd -> Минимальный индекс, такой что mas[minInd] <= value
     */
    // Pre: (mas.Sorted) && (value in Z) && (mas.length > 0)
    // Post: ((R == minInd) && (mas[minInd] == value)) ||
    //       || ((R == (-1 * minInd - 1)) && ((mas[minInd] != value) || mas[minInd] не существует))
    public static int recursiveSearch(int[] mas, int value){
        //(mas.Sorted) && (value in Z)
        return recursiveSearch(mas, value, -1, mas.length);
        // Post: ((R == minInd) && (mas[minInd] == value)) ||
        //       || ((R == (-1 * minInd - 1)) && ((mas[minInd] != value) || mas[minInd] не существует))
    }

    // Pre: (mas.Sorted) && (value in Z) && (left == left') && (right == right') && ((right - left) != 1) && (minInd in [left'; right'])
    // Post: ((R == minInd) && (mas[minInd] == value)) ||
    //       || ((R == (-1 * minInd - 1)) && ((mas[minInd] != value) || mas[minInd] не существует))
    //Invariant :  mas.Sorted && Immutable && (mas[right] <= value < mas[left]) && (mas[-1] == +inf) && (mas[mas.length] == -inf)
    private static int recursiveSearch(int[] mas, int value, int left, int right){
        //(right == right') && (left == left') && (minInd in [left'; right'])
        if (right - left == 1){
            // (right - left == 1) && (right == right') && (left == left') && (minInd in [left'; right'])
            // -> minInd == right'
            if ((right >= mas.length) || (mas[right] != value)){
                // ((right' >= mas.length) || (mas[right'] != value)) && (minInd == right')
                return -1 * right - 1;
                // (R == (-1 * minInd - 1)) && ((mas[minInd] != value) || (mas[minInd] не существует))
            }
            // (minInd == right') && (right < mas.length) && (mas[minInd] == value)
            return right;
            // (R == minInd) && (mas[minInd] == value)
        }
        // ((right - left) != 1) && (minInd in [left'; right'])
        int mid = (left + right) / 2;
        // ((right - left) != 1) && (minInd in [left'; right']) && (mid = (left + right) / 2)
        if (mas[mid] > value){
            // ((right - left) != 1) && (minInd in [mid; right]) && (mid = (left + right) / 2)
            return recursiveSearch(mas, value, mid, right);
            // ((R == -1 * minInd - 1) && (mas[minInd] != value)) || ((R == minInd) && (mas[minInd] == value))
        }
        else {
            // ((right - left) != 1) && (minInd in [left; mid]) && (mid = (left + right) / 2)
            return recursiveSearch(mas, value, left, mid);
            // ((R == (-1 * minInd - 1)) && (mas[minInd] != value)) || ((R == minInd) && (mas[minInd] == value))
        }
    }

    // Pre: mas.Sorted && (value in Z)
    // Post: ((R == minInd) && (mas[minInd] == value)) ||
    //       || ((R == (-1 * minInd - 1)) && ((mas[minInd] != value) || mas[minInd] не существует))
    // Invariant :  (mas.Sorted) && Immutable && (mas[right] <= value < mas[left])
    public static int iterativeSearch(int[] mas, int value){
        int left = -1;
        // left = -1
        int right = mas.length;
        // (left = -1) && (right = mas.length)
        while (right - left > 1){
            // ((right - left) > 1) && (minInd in [left; right]) && (right == right') && (left == left')
            int mid = (right + left) / 2;
            // (mid == (right + left) / 2) && ((right - left) > 1) && (minInd in [left; right]) && (right == right')
            // && (left == left')
            if (mas[mid] > value){
                // (mas[mid] > value) && (mid == (right + left) / 2) && ((right - left) > 1) && (minInd in [mid; right])
                // && (right == right') && (left == left')
                left = mid;
                // (mas[mid] > value) && (mid = (right + left) / 2) && (right - left > 1) && (minInd in [mid; right])
                // && (left' == mid) && (right == right') && (left == left')
            }
            else{
                //(mas[mid] <= value) && (mid = (right + left) / 2) && ((right - left) > 1) && (minInd in [left; mid]) &&
                // && (right == right') && (left == left')
                right = mid;
                // (mas[mid] <= value) && (mid = (right + left) / 2) && ((right - left) > 1) && (minInd in [left; mid]) &&
                // && (right' == mid) && (right == right') && (left == left')
            }
            // minInd in [left'; right']
        }
        // ((right - left) <= 1) && (minInd in [left'; right']) && (right <= mas.length)
        // -> (minInd == right') && (mas[minInd] <= value)
        if (right == mas.length){
            // (right == mas.length) -> (value < (любой элемент из mas)) && (minInd == right)
            return -1 * right - 1;
            // (R == -1 * minInd - 1) && (mas[minInd] не существует)
        }
        // (minInd == right') && (right < mas.length)
        if ((mas[right] != value)){
            // (minInd == right') && (right < mas.length) && (mas[minInd] != value)
            return -1 * right - 1;
            // (R == (-1 * minInd - 1)) && (mas[minInd] != value)
        }
        // (minInd == right') && (mas[minInd] == value) && (right < mas.length)
        return right;
        // (R == minInd) && (mas[minInd] == value)
    }
}
