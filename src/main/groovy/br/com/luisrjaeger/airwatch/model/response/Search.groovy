package br.com.luisrjaeger.airwatch.model.response

class Search {

    public List<Application> Application

    class Application {

        Id Id

        public String Uuid

        public String ApplicationName

        public String BundleId

        public String AppVersion

        public Integer LocationGroupId

        Integer getId() {
            return Id.Value
        }
    }

}
