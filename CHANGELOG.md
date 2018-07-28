# 0.7.0

* **BREAKING** Change `close()` to be `cancel(null)` instead of detach.
* **BREAKING** A thread *always* has a `Ctx`, detach causes a new empty to be attached.
* Contexts are no longer immutable.  Although the API hasn't changed and there is still no locking for reads, there
  is no longer a need to worry about `Ctx` references changing every time a value gets added.
* Add `Ctx#cancel(Throwable)` to cancel with a cause
* Add `Ctx#detach` to replace `Ctx#close` for detaching a Ctx
* Add `Ctx.Key#get` and `Ctx.Key#set` convenience operations
* Add `Ctx#with(Map<String, T>, Class<T>)` for quickly setting values from a map
* Add `Ctx#add(Map<String, T>, Class<T>)` alias to `Ctx#with(Map<String, T>, Class<T>)` for more elegant Fluent phrasing
* Add `Ctx#isEmpty` to determine if a context has any values set
* Add `Ctx#create` alias to `Ctx#empty` for more elegant Fluent phrasing
* Add `Ctx#add(Key<T>, T)` alias to `Ctx#with` for more elegant Fluent phrasing

# 0.3.0

* Starting a timeout now creates a new Ctx with the timeout, rather
  than putting the timeout on the current context.
