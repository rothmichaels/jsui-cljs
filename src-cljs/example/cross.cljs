;; Cross example: refer to the Javascript original
;; [here](http://www.cycling74.com/docs/max6/dynamic/c74_docs.html#jsmgraphics).

(ns example.cross)

(defn calc-aspect
  "
Aspect ratio calculation: height always ranges from `-1.0` to `1.0`.

The value of `this` seems not to be predictable at all function call
points (at least, not predictable to me), so for safety we pass it everywhere
as `me`.

Original code:

    function calcAspect() {
        var width = this.box.rect[2]
                    - this.box.rect[0];
        var height = this.box.rect[3]
                     - this.box.rect[1];
        return width/height;
    }
"
  [me]
  (let [rect (.-rect (.-box me))
        width (- (nth rect 2) (nth rect 0))
        height (- (nth rect 3) (nth rect 1))]
    (/ width height)))

(defn line
  "Convenience function, for threading: call `.line_to`, return `mgraphics` context."
  [m x y]
  (.line_to m x y)
  m)

(defn move
  "Convenience function, for threading: call `.move_to`, return `mgraphics` context."
  [m x y]
  (.move_to m x y)
  m)

(defn stroke
  "Convenience function, for threading: call `.stroke`, return `mgraphics` context."
  [m]
  (.stroke m)
  m)

(defn paint
  "
Paint function. Calculate aspect ratio, draw a cross. Again,
`me` is passed in explicitly.

We can get the MGraphics context via `js/mgraphics` - it's in the JS namespace -
but I prefer doing it explicitly from `this`.
"
  [me]
  (let [aspect (calc-aspect me)]
    (-> (.-mgraphics me)
        (move (- aspect) -1.0)
        (line aspect 1.0)
        (stroke)
        (move aspect -1.0)
        (line (- aspect) 1.0)
        (stroke))))

(defn setup
  "
Set up the context, including `mgraphics`. Set `autowatch` to
`1` (to allow auto-reloading)
and plant the (arg-less) `paint` function at the top level.

Original `mgraphics` setup code:

    mgraphics.init();
    mgraphics.relative_coords = 1;
    mgraphics.autofill = 0;
"
  [me]
  (.init (.-mgraphics me))
  (set! (.-relative_coords (.-mgraphics me)) 1)
  (set! (.-autofill (.-mgraphics me)) 0)
  (set! (.-paint me) (fn [] (paint me)))
  (set! (.-autowatch me) 1)
  (let [d (js/Date.)]
    (js/post (str "Loaded example.cross at " d "\n"))))
