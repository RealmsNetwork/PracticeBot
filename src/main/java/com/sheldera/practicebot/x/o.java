package com.sheldera.practicebot.x;

record o(String bq, String br, String bs) {

   boolean Q() {
      return this.br != null && !this.br.isBlank() && this.bs != null && !this.bs.isBlank();
   }

   public String R() {
      return this.bq;
   }

   public String texture() {
      return this.br;
   }

   public String signature() {
      return this.bs;
   }
}
