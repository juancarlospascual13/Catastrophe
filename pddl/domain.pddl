(define (domain Nuclear)
(:requirements :typing :multi-agent :unfactored-privacy :action-costs)
(:types
       waypoint - object
           dump - waypoint
       pickable - object
           machine - pickable
               cleaner - machine
               drone - machine
           rubble - pickable
)
(:predicates
             (at ?p - pickable ?w - waypoint)
             (assessed ?r - rubble)
             (is_radioactive ?r - rubble)
             (is_clean ?r - pickable)
             (is_active ?m - machine)
             (is_broken ?m - machine)
             (traversable_land ?x - waypoint ?y - waypoint)
             (traversable_flight ?x - waypoint ?y - waypoint)
             (empty ?c - cleaner)
             (full ?p - pickable ?c - cleaner)
)

(:functions
	(total-cost) - number
	(distance ?y - waypoint ?z - waypoint) - number
	(pick_rubble ?r - rubble) - number
	(pick_machine ?m - machine) - number
)

(:action walk
:agent ?cleaner - cleaner
:parameters (?y - waypoint ?z - waypoint)
:precondition (and
                   (is_active ?cleaner)
                   (traversable_land ?y ?z)
                   (at ?cleaner ?y)
              )
:effect (and
             (not (at ?cleaner ?y))
             (at ?cleaner ?z)
	     	(increase (total-cost) (distance ?y ?z))
        )
)

(:action fly
:agent ?drone - drone
:parameters (?y - waypoint ?z - waypoint)
:precondition (and
                   (is_active ?drone)
                   (traversable_flight ?y ?z)
                   (at ?drone ?y)
              )
:effect (and
             (not (at ?drone ?y))
             (at ?drone ?z)
		(increase (total-cost) (distance ?y ?z))
        )
)

(:action assess
:agent ?drone - drone
:parameters (?r - rubble ?w - waypoint)
:precondition (and
              (is_active ?drone)
              (at ?r ?w)
              (at ?drone ?w)
              )
:effect (and
        (assessed ?r)
        )
)

(:action pickup_rubble
:agent ?cleaner - cleaner
:parameters (?r - rubble ?x - waypoint)
:precondition (and
              (is_active ?cleaner)
              (at ?cleaner ?x)
              (at ?r ?x)
              (empty ?cleaner)
              )
:effect (and
        (not (empty ?cleaner))
        (full ?r ?cleaner)
        (not (at ?r ?x))
	(increase (total-cost) (pick_rubble ?r))
        )
)

(:action pickup_machine
:agent ?cleaner - cleaner
:parameters (?r - machine ?x - waypoint)
:precondition (and
              (is_active ?cleaner)
              (at ?cleaner ?x)
              (at ?r ?x)
              (is_broken ?r)
              (empty ?cleaner)
              )
:effect (and
        (not (empty ?cleaner))
        (full ?r ?cleaner)
        (not (at ?r ?x))
        (increase (total-cost) (pick_machine ?r))
        )
)

(:action drop
:agent ?cleaner - cleaner
:parameters (?pickable - pickable ?w - dump)
:precondition (and
              (is_active ?cleaner)
              (full ?pickable ?cleaner)
              (at ?cleaner ?w)
              )
:effect (and
        (not (full ?pickable ?cleaner))
        (empty ?cleaner)
        (at ?pickable ?w)
        (is_clean ?pickable)
        )
)

(:action place_at
:agent ?cleaner - cleaner
:parameters (?pickable - pickable ?w - waypoint)
:precondition (and
              (is_active ?cleaner)
              (full ?pickable ?cleaner)
              (at ?cleaner ?w)
              )
:effect (and
        (not (full ?pickable ?cleaner))
        (empty ?cleaner)
        (at ?pickable ?w)
        )
)
)
