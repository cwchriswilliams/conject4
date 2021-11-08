# conject4

Connect 4 Clojure Project

This is a research project to try specific clojure tooling patterns. There are better implementations available.

# Observations

## Spec

- Generative testing is very useful
- Main question is how much spec is too much spec
- Can some degree of spec replace documentation? e.g. if your arg1 is a board containing :height and :width, is it better to have a spec saying "This method requires a map with :height and :width" which can be validated with tooling and still generate documentation vs a textual comment? (note would only be able to replace args and ret documentation. Could not replace justification comments or summary comments)


# TODO

- Custom generators for spec (Maybe for generating a board? This works currently, but is slow)
- Player Interaction
- AI
    - Random
    - Minimax
    - ???