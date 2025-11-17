package com.example.simplefantasyworld

// --- 1. TEMEL VERİ VE SÖZLEŞMELER ---

// `interface` (Arayüz): Bir "yetenek" sözleşmesidir.
// Bu arayüzü uygulayan her sınıf, saldir() metodunu yazmak zorundadır.
interface Saldirabilir {
    fun saldir(hedef: Karakter)
}

// `enum class`: Bir değişkenin alabileceği sabit ve sınırlı seçenekleri tanımlar.
enum class KarakterTipi {
    SAVASCI,
    BUYUCU,
    OKCU
}

// `data class`: Sadece veri tutmak için kullanılır.
// equals(), hashCode(), toString() ve copy() metotlarını otomatik oluşturur.
data class Esya(val ad: String, val deger: Int)


// --- 2. JENERİK ENVANTER SINIFI ---

// `generic class` (Jenerik Sınıf): Herhangi bir tiple (`<T>`) çalışabilen sınıf.
// Bu envanter, Esya, Zırh, Silah vb. her şeyi tutabilir.
class Envanter<T> {

    // `encapsulation` (Kapsülleme) ve `private` kullanımı:
    // 'esyalar' listesi dış dünyadan gizlenmiştir (private).
    // Ona sadece 'ekle' ve 'listele' gibi public metotlarla erişilebilir.
    private val esyalar = mutableListOf<T>()

    fun ekle(esya: T) {
        esyalar.add(esya)
        println("[$esya] envantere eklendi.")
    }

    // `high order function` (Üst Seviye Fonksiyon):
    // Parametre olarak bir 'lambda' (fonksiyon) alır.
    // 'kural' parametresi, (T) -> Boolean tipinde bir fonksiyondur.
    fun filtrele(kural: (T) -> Boolean): List<T> {
        return esyalar.filter(kural)
    }

    fun listele() {
        println("--- Envanter ---")
        esyalar.forEach { println(it) }
        println("----------------")
    }
}


// --- 3. SOYUT TEMEL KARAKTER SINIFI ---

// `abstract class` (Soyut Sınıf):
// Doğrudan nesnesi oluşturulamayan (new Karakter() YASAK) bir şablondur.
// Alt sınıflar (Savasci, Buyucu) için ortak bir temel oluşturur.
abstract class Karakter(
    // `public` (varsayılan) özellik: Her yerden erişilebilir.
    val isim: String,

    // `protected` özellik: Sadece bu sınıf ve alt sınıfları (Savasci vb.) erişebilir.
    protected var saglik: Int,

    val tip: KarakterTipi
) {
    // `public` metot: Dışarıdan saglik durumunu kontrollü okumak için.
    // Bu da 'encapsulation' ilkesinin bir parçasıdır.
    fun mevcutSaglik(): Int {
        return saglik
    }

    // `open` metot: Alt sınıfların bu metodu `override` edebileceğini (ezebileceğini) belirtir.
    open fun hasarAl(hasar: Int) {
        if (saglik > 0) {
            saglik -= hasar
            if (saglik <= 0) {
                saglik = 0
                println("$isim yenildi!")
            } else {
                println("$isim $hasar hasar aldı. Kalan sağlık: $saglik")
            }
        }
    }

    // `abstract` (Soyut) Metot:
    // Gövdesi yoktur. Bu sınıfı miras alan her sınıf, bu metodu `override` etmek ZORUNDADIR.
    abstract fun ozelYetenek()
}


// --- 4. SOMUT SINIFLAR (MİRAS ALMA) ---

// `class` (Standart Sınıf) ve `inheritance` (Kalıtım):
// 'Savasci' sınıfı, 'Karakter' sınıfından miras alır VE 'Saldirabilir' arayüzünü uygular.
class Savasci(
    // `constructor` (Birincil Kurucu):
    // Nesne oluşturulurken alınan temel parametreler.
    isim: String,
    private val zirhGucu: Int // Sadece Savasci'ya özel private özellik

) : Karakter(isim, 150, KarakterTipi.SAVASCI), Saldirabilir {

    // `override` (Metot Ezme):
    // 'Karakter' sınıfındaki 'ozelYetenek' soyut metodunu uygular (ezmek zorundadır).
    override fun ozelYetenek() {
        println("$isim 'Kılıç Kalkanı' yeteneğini kullandı! Zırhı arttı.")
    }

    // `override`: 'Saldirabilir' arayüzündeki metodu uygular.
    override fun saldir(hedef: Karakter) {
        val hasar = 30
        println("$isim, ${hedef.isim}'a kılıçla saldırdı ($hasar hasar).")
        hedef.hasarAl(hasar)
    }

    // `override` ve `super` kullanımı:
    // 'Karakter' sınıfındaki 'hasarAl' metodunu eziyoruz.
    override fun hasarAl(hasar: Int) {
        // Zırh gücü sayesinde daha az hasar alıyor.
        val alinanNetHasar = hasar - zirhGucu

        // `super`: Üst sınıfın (Karakter) orijinal 'hasarAl' metodunu çağırır.
        println("$isim zırhı sayesinde $zirhGucu hasar engelledi.")
        super.hasarAl(alinanNetHasar)
    }
}

// Diğer bir somut sınıf
class Buyucu : Karakter {

    // `private` özellik: Encapsulation. Mana, dışarıdan doğrudan değiştirilemez.
    private var mana: Int = 100

    // `constructor` (İkincil Kurucu):
    // 'Buyucu' sınıfı, birincil kurucu yerine ikincil kurucu kullanıyor.
    // 'this(isim, ...)' ile üst sınıfı (Karakter) çağırmak zorundadır.
    constructor(isim: String) : super(isim, 80, KarakterTipi.BUYUCU) {
        println("Bir Büyücü ($isim) dünyaya geldi.")
    }

    // `override`: Zorunlu olan soyut metot eziliyor.
    override fun ozelYetenek() {
        if (mana >= 40) {
            mana -= 40
            println("$isim 'Ateş Topu' büyüsü yaptı! Kalan mana: $mana")
        } else {
            println("$isim'in yeterli manası kalmadı.")
        }
    }
}


// --- 5. SINGLETON NESNE ---

// `object` (Singleton):
// Bu sınıftan tüm program boyunca sadece BİR TANE nesne oluşturulur.
// Genellikle yardımcı (utility) fonksiyonlar veya oyun yöneticisi için kullanılır.
object DunyaYardimcisi {

    fun savasBaslat(saldiran: Saldirabilir, savunan: Karakter) {
        println("\n--- SAVAŞ BAŞLIYOR! ---")
        println("${(saldiran as Karakter).isim} vs ${savunan.isim}")
        saldiran.saldir(savunan)
        println("------------------------\n")
    }
}


// --- 6. PROJENİN ÇALIŞTIRILMASI (ANA FONKSİYON) ---

fun main() {
    println("### Fantezi Dünyası Oyunu Başladı ###\n")

    // 1. Nesneleri Oluşturma (Constructor kullanımı)
    val aragorn = Savasci("Aragorn", 10)
    val gandalf = Buyucu("Gandalf")

    // 2. Data Class Kullanımı
    val iksir = Esya("Sağlık İksiri", 50)
    val kilic = Esya("Elf Kılıcı", 200)

    // 3. Generic Class Kullanımı (Envanter<Esya>)
    val savasciEnvanteri = Envanter<Esya>()
    savasciEnvanteri.ekle(iksir)
    savasciEnvanteri.ekle(kilic)
    savasciEnvanteri.listele()

    // 4. Lambda ve High-Order Function Kullanımı
    // 'filtrele' fonksiyonuna bir 'lambda' ({...}) yolluyoruz.
    // 'it' lambdanın tek parametresidir (Esya).
    val pahaliEsyalar = savasciEnvanteri.filtrele { it.deger > 100 }
    println("Pahalı Eşyalar: $pahaliEsyalar")

    // 5. Inheritance, Polymorphism ve Override Kullanımı
    aragorn.ozelYetenek() // Savasci'nın ezdiği metot
    gandalf.ozelYetenek() // Buyucu'nun ezdiği metot

    // 6. Object (Singleton) ve Interface Kullanımı
    // DunyaYardimcisi.savasBaslat(gandalf, aragorn) // HATA! Buyucu, 'Saldirabilir' arayüzünü UYGULAMADI.
    DunyaYardimcisi.savasBaslat(aragorn, gandalf)

    // 7. Override edilen 'hasarAl' ve 'super' kullanımı
    // Gandalf'ın yeteneği 40 hasar vursun diyelim
    println("Gandalf, Aragorn'a büyü yapıyor (40 hasar).")
    aragorn.hasarAl(40) // Savasci'nın ezdiği 'hasarAl' çalışacak

    // 8. Encapsulation Kontrolü
    println("\nSavaş Sonrası Durum:")
    println("Aragorn Sağlık: ${aragorn.mevcutSaglik()}")
    println("Gandalf Sağlık: ${gandalf.mevcutSaglik()}")

    // gandalf.mana = 1000 // HATA! 'mana' private olduğu için dışarıdan erişilemez.
}