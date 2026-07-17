(ns culture.facts
  "Country-level regional-culture catalog for Bhutan (BTN) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"BTN"
   [{:culture/id "btn.dish.ema-datshi"
     :culture/name "Ema datshi"
     :culture/country "BTN"
     :culture/kind :dish
     :culture/summary "Spicy stew of hot chili peppers and cheese, among the most famous dishes in Bhutanese cuisine and recognised as the national dish of Bhutan."
     :culture/url "https://en.wikipedia.org/wiki/Ema_datshi"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.dish.momo"
     :culture/name "Momo"
     :culture/country "BTN"
     :culture/kind :dish
     :culture/summary "Steamed filled dumpling of Nepalese and Tibetan cuisine, also popular in neighbouring Bhutan, Bangladesh and India."
     :culture/url "https://en.wikipedia.org/wiki/Momo_(food)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.dish.hoentay"
     :culture/name "Hoentay"
     :culture/country "BTN"
     :culture/kind :dish
     :culture/summary "Traditional buckwheat dumpling originating from the Haa Valley in Bhutan, typically eaten during the Lomba harvest festival and New Year celebrations."
     :culture/url "https://en.wikipedia.org/wiki/Hoentay"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.product.red-rice"
     :culture/name "Bhutanese red rice"
     :culture/country "BTN"
     :culture/kind :product
     :culture/summary "Medium-grain red japonica rice grown in the Kingdom of Bhutan in the eastern Himalayas, the staple rice of the Bhutanese people."
     :culture/url "https://en.wikipedia.org/wiki/Bhutanese_red_rice"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.product.chhurpi"
     :culture/name "Chhurpi"
     :culture/country "BTN"
     :culture/kind :product
     :culture/summary "Traditional Himalayan cheese consumed in Nepal, Bhutan, Tibet and parts of northeastern India; the hard variety is among the hardest cheeses in the world."
     :culture/url "https://en.wikipedia.org/wiki/Chhurpi"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.beverage.ara"
     :culture/name "Ara"
     :culture/country "BTN"
     :culture/kind :beverage
     :culture/summary "Traditional alcoholic beverage of fermented or distilled rice, barley, maize, millet or wheat, consumed in Bhutan and the Indian state of Arunachal Pradesh."
     :culture/url "https://en.wikipedia.org/wiki/Ara_(drink)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.beverage.suja"
     :culture/name "Butter tea"
     :culture/name-local "Suja"
     :culture/country "BTN"
     :culture/kind :beverage
     :culture/summary "Himalayan drink of tea, butter and salt, known in Bhutan as suja and drunk at special occasions such as weddings and Losar."
     :culture/url "https://en.wikipedia.org/wiki/Butter_tea"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.festival.tshechu"
     :culture/name "Tshechu"
     :culture/country "BTN"
     :culture/kind :festival
     :culture/summary "Annual religious Bhutanese festivals held in each district (dzongkhag) of Bhutan, featuring masked Cham dances and the unfurling of large thongdrel scroll paintings."
     :culture/url "https://en.wikipedia.org/wiki/Tshechu"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "btn.heritage.paro-taktsang"
     :culture/name "Paro Taktsang"
     :culture/country "BTN"
     :culture/kind :heritage
     :culture/summary "Sacred Buddhist monastery complex (Tiger's Nest) on a cliffside of the Paro Valley, described as the cultural icon of Bhutan; the present monastery was established in 1692."
     :culture/url "https://en.wikipedia.org/wiki/Paro_Taktsang"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-btn culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "BTN"))
                 " BTN entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
