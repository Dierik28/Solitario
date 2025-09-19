package DeckOfCards;

public class Pila <T>{
    private T[] pila;
    private int tope;

    public Pila(int tamano) {
        pila = (T[]) new Object[tamano];
        tope = -1;
    }

    public void push(T dato){
        if(tope < pila.length-1){
            tope++;
            pila[tope] = dato;
        }else{
            System.out.println("Desbordamiento");
        }
    }

    public T pop(){
        if(tope > -1){
            tope-=1;
            return pila[tope+1];
        }else{
            System.out.println("Subdesbordamiento");
            return null;
        }
    }

    public T peek(){
        if(tope == -1){
            return null;
        }
        return pila[tope];
    }

    public boolean estaVacia(){
        return tope == -1;
    }

    public boolean estaLlena(){
        return tope == pila.length-1;
    }

    public int tamano(){
        return tope+1;
    }
}
