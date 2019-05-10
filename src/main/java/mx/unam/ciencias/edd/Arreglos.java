package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador) {
      quickSort(arreglo, comparador, arreglo.length-1, 0, arreglo.length-1);
    }
    private static <T> void
    quickSort(T[] a, Comparator<T> c, int pib, int izq, int der){
      if(pib <= izq)
        return;
      while(c.compare(a[izq], a[pib]) < 0)
        izq++;
      while(c.compare(a[der], a[pib]) >= 0){
          if(der--==izq){
            swap(a, izq, pib);
            quickSort(a, c, der, 0, der); //izq
            quickSort(a, c, pib, izq+1, pib); //der
            return;
          }
      }
      swap(a, izq, der);
      quickSort(a, c, pib, izq, der);
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador) {
      for (int alfa=0; alfa<arreglo.length; alfa++) {
        int min = alfa;
        for (int beta=alfa; beta<arreglo.length; beta++)
          if(comparador.compare(arreglo[beta], arreglo[min]) < 0)
            min = beta;

        swap(arreglo, alfa, min);
      }
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
      if(arreglo.length < 1)
            return -1;
      return busquedaBinaria(arreglo, elemento, comparador, 0, arreglo.length-1);
    }
    private static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador, int inicio, int fin) {
        int raiz = (int) inicio+((fin-inicio)/2);
        if (comparador.compare(elemento, arreglo[raiz]) == 0)
          return raiz;
        if((fin-inicio) == 0 || fin < 0)
          return -1;
        if (comparador.compare(elemento, arreglo[raiz]) < 0)
          return busquedaBinaria(arreglo, elemento, comparador, inicio, raiz-1);

        return busquedaBinaria(arreglo, elemento, comparador, raiz+1, fin);
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }

    private static <T> void swap(T[] a, int izq, int der){
      T swap = a[izq];
      a[izq] = a[der];
      a[der] = swap;
    }
}
