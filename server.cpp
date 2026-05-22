#include "crow_all.h"

int main() {

  crow::SimpleApp app;

  CROW_ROUTE(app, "/")([]() { return "NeilOS C++ Backend Running"; });

  app.port(18080).multithreaded().run();
}