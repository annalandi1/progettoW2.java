import java.util.*;

//creo le eccezioni per i casi in cui non trovo o é duplicato l'elemento
class ElementoNonTrovatoException extends Exception {
    public ElementoNonTrovatoException(String message) {
        super(message);
    }
}

class IsbnDuplicatoException extends Exception {
    public IsbnDuplicatoException(String message) {
        super(message);
    }
}

// creo la classe astratta Elemento e i suoi attributi, in questo caso l'isbn é la chiave valore, ovvero l'identificatore univoco
abstract class Elemento {
    private String isbn; //International Standard Book Number
    private String titolo;
    private int annoPubblicazione;
    private int numeroPagine;

    public Elemento(String isbn, String titolo, int annoPubblicazione, int numeroPagine) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.numeroPagine = numeroPagine;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public int getNumeroPagine() {
        return numeroPagine;
    }

    @Override
    public String toString() {
        return "ISBN: " + isbn + ", Titolo: " + titolo + ", Anno: " + annoPubblicazione + ", Pagine: " + numeroPagine;
        //uso il metodo toString() per stampare i dettagli dell'elemento
    }
}


//la classe Libro estende Elemento e aggiugo l'attributo specifico Autore
class Libro extends Elemento {
    private String autore;

    public Libro(String isbn, String titolo, int annoPubblicazione, int numeroPagine, String autore) {
        super(isbn, titolo, annoPubblicazione, numeroPagine);
        this.autore = autore;
    }

    public String getAutore() {
        return autore;
    }

    @Override
    public String toString() {
        return super.toString() + ", Autore: " + autore;
    }
}


//la classe Rivista estende Elemento e aggiugo l'attributo specifico Periodicita
class Rivista extends Elemento {
    private String periodicita;

    public Rivista(String isbn, String titolo, int annoPubblicazione, int numeroPagine, String periodicita) {
        super(isbn, titolo, annoPubblicazione, numeroPagine);
        this.periodicita = periodicita;
    }

    public String getPeriodicita() {
        return periodicita;
    }

    @Override
    public String toString() {
        return super.toString() + ", Periodicità: " + periodicita;
    }
}

//gestisco la logica del catalogo in questa classe, utilizzando Map per identificare l'elemento tramite ibsn
class Catalogo {
    private Map<String, Elemento> elementi = new HashMap<>();

    public void aggiungiElemento(Elemento elemento) throws IsbnDuplicatoException {
        if (elementi.containsKey(elemento.getIsbn())) {
            throw new IsbnDuplicatoException("Elemento con ISBN " + elemento.getIsbn() + " già presente nel catalogo.");
        }
        elementi.put(elemento.getIsbn(), elemento);
    }

    public Elemento cercaPerIsbn(String isbn) throws ElementoNonTrovatoException {
        if (!elementi.containsKey(isbn)) {
            throw new ElementoNonTrovatoException("Elemento con ISBN " + isbn + " non trovato.");
        }
        return elementi.get(isbn);
    }

    public void rimuoviElemento(String isbn) throws ElementoNonTrovatoException {
        if (!elementi.containsKey(isbn)) {
            throw new ElementoNonTrovatoException("Elemento con ISBN " + isbn + " non trovato.");
        }
        elementi.remove(isbn);
    }

    public List<Elemento> ricercaPerAutore(String autore) {
        List<Elemento> risultati = new ArrayList<>();
        for (Elemento elemento : elementi.values()) {
            if (elemento instanceof Libro && ((Libro) elemento).getAutore().equalsIgnoreCase(autore)) {
                risultati.add(elemento);
            }
        }
        return risultati;
    }

    public List<Elemento> ricercaPerAnnoPubblicazione(int anno) {
        List<Elemento> risultati = new ArrayList<>();
        for (Elemento elemento : elementi.values()) {
            if (elemento.getAnnoPubblicazione() == anno) {
                risultati.add(elemento);
            }
        }
        return risultati;
    }

    public void aggiornaElemento(String isbn, Elemento nuovoElemento) throws ElementoNonTrovatoException {
        if (!elementi.containsKey(isbn)) {
            throw new ElementoNonTrovatoException("Elemento con ISBN " + isbn + " non trovato.");
        }
        elementi.put(isbn, nuovoElemento);
    }

    public void statisticheCatalogo() {
        long numLibri = elementi.values().stream().filter(e -> e instanceof Libro).count();
        long numRiviste = elementi.values().stream().filter(e -> e instanceof Rivista).count();
        Optional<Elemento> elementoConMaggiorPagine = elementi.values().stream()
                .max(Comparator.comparingInt(Elemento::getNumeroPagine));
        double mediaPagine = elementi.values().stream()
                .mapToInt(Elemento::getNumeroPagine)
                .average()
                .orElse(0);

        System.out.println("Numero totale di libri: " + numLibri);
        System.out.println("Numero totale di riviste: " + numRiviste);
        System.out.println("Elemento con maggior numero di pagine: " + elementoConMaggiorPagine.orElse(null));
        System.out.println("Media delle pagine: " + mediaPagine);
    }
}


//la classe Main gestisce interazione con l'utente tramite un menú:
public class Main {
    public static void main(String[] args) {
        Catalogo catalogo = new Catalogo();
        Scanner scanner = new Scanner(System.in);

        try {
            //esempio di utilizzo
            catalogo.aggiungiElemento(new Libro("123", "Il Signore degli Anelli", 1954, 1200, "J.R.R. Tolkien"));
            catalogo.aggiungiElemento(new Rivista("456", "Science Magazine", 2023, 50, "Mensile"));
        } catch (IsbnDuplicatoException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            System.out.println("1. Aggiungi libro");
            System.out.println("2. Aggiungi rivista");
            System.out.println("3. Cerca per International Standard Book Number");
            System.out.println("4. Rimuovi elemento");
            System.out.println("5. Ricerca per autore");
            System.out.println("6. Ricerca per anno");
            System.out.println("7. Aggiorna elemento");
            System.out.println("8. Statistiche");
            System.out.println("9. Esci");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma la newline

            try {
                switch (scelta) { //gestisco gli input tramite scanner e switch, breck consente di uscire dallo switch
                    case 1:
                        System.out.println("Inserisci International Standard Book Number, titolo, anno, numero di pagine e autore:");
                        String isbnLibro = scanner.nextLine();
                        String titoloLibro = scanner.nextLine();
                        int annoLibro = scanner.nextInt();
                        int pagineLibro = scanner.nextInt();
                        scanner.nextLine(); // Consuma la newline
                        String autore = scanner.nextLine();
                        catalogo.aggiungiElemento(new Libro(isbnLibro, titoloLibro, annoLibro, pagineLibro, autore));
                        break;
                    case 2:
                        System.out.println("Inserisci ISBN, titolo, anno, numero di pagine e periodicità:");
                        String isbnRivista = scanner.nextLine();
                        String titoloRivista = scanner.nextLine();
                        int annoRivista = scanner.nextInt();
                        int pagineRivista = scanner.nextInt();
                        scanner.nextLine(); // Consuma la newline
                        String periodicita = scanner.nextLine();
                        catalogo.aggiungiElemento(new Rivista(isbnRivista, titoloRivista, annoRivista, pagineRivista, periodicita));
                        break;
                    case 3:
                        System.out.println("Inserisci ISBN:");
                        String isbnCerca = scanner.nextLine();
                        System.out.println(catalogo.cercaPerIsbn(isbnCerca));
                        break;
                    case 4:
                        System.out.println("Inserisci ISBN:");
                        String isbnRimuovi = scanner.nextLine();
                        catalogo.rimuoviElemento(isbnRimuovi);
                        break;
                    case 5:
                        System.out.println("Inserisci autore:");
                        String autoreCerca = scanner.nextLine();
                        System.out.println(catalogo.ricercaPerAutore(autoreCerca));
                        break;
                    case 6:
                        System.out.println("Inserisci anno di pubblicazione:");
                        int annoCerca = scanner.nextInt();
                        System.out.println(catalogo.ricercaPerAnnoPubblicazione(annoCerca));
                        break;
                    case 7:
                        System.out.println("Inserisci ISBN dell'elemento da aggiornare:");
                        String isbnAggiorna = scanner.nextLine();
                        System.out.println("Inserisci i nuovi dettagli dell'elemento (1 per libro, 2 per rivista):");
                        int tipoElemento = scanner.nextInt();
                        scanner.nextLine(); // Consuma la newline
                        if (tipoElemento == 1) {
                            System.out.println("Inserisci titolo, anno, numero di pagine e autore:");
                            String nuovoTitoloLibro = scanner.nextLine();
                            int nuovoAnnoLibro = scanner.nextInt();
                            int nuovePagineLibro = scanner.nextInt();
                            scanner.nextLine(); // Consuma la newline
                            String nuovoAutore = scanner.nextLine();
                            catalogo.aggiornaElemento(isbnAggiorna, new Libro(isbnAggiorna, nuovoTitoloLibro, nuovoAnnoLibro, nuovePagineLibro, nuovoAutore));
                        } else if (tipoElemento == 2) {
                            System.out.println("Inserisci titolo, anno, numero di pagine e periodicità:");
                            String nuovoTitoloRivista = scanner.nextLine();
                            int nuovoAnnoRivista = scanner.nextInt();
                            int nuovePagineRivista = scanner.nextInt();
                            scanner.nextLine(); // Consuma la newline
                            String nuovaPeriodicita = scanner.nextLine();
                            catalogo.aggiornaElemento(isbnAggiorna, new Rivista(isbnAggiorna, nuovoTitoloRivista, nuovoAnnoRivista, nuovePagineRivista, nuovaPeriodicita));
                        } else {
                            System.out.println("Tipo di elemento non valido.");
                        }
                        break;
                    case 8:
                        catalogo.statisticheCatalogo();
                        break;
                    case 9:
                        System.out.println("Arrivederci!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Scelta non valida. Riprova.");
                }
            } catch (ElementoNonTrovatoException | IsbnDuplicatoException e) {
                System.out.println("Errore: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Errore: input non valido. Riprova.");
                scanner.nextLine(); // Consuma l'input non valido
            }
        }
    }
}
