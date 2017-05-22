#include "move.h"

#include <unistd.h>
#include <stdlib.h>
#include <assert.h>
#include <sstream>
#include <ncurses.h>

#include "dungeon.h"
#include "heap.h"
#include "move.h"
#include "npc.h"
#include "pc.h"
#include "character.h"
#include "utils.h"
#include "path.h"
#include "io.h"

int do_combat(dungeon_t *d, character *atk, character *def, int type)
{
  // type: 0 for weapon, 1 for ranged, 2 for spell
  int dam = 0;
  std::stringstream status;
  //NPC v. NPC does nothing
  if(atk != d->pc && def != d->pc) {
    return 1;
  }
  if (atk == d->pc) {
    if (type == 0) {
      if (((pc *)(d->pc))->equip_slot[0]) {
	dam = ((pc *)(d->pc))->equip_slot[0]->roll_dice();
      } else {
	dam = d->pc->damage->roll();
      }
    } else if (type == 1) {
      dam = ((pc *)(d->pc))->equip_slot[2]->roll_dice();
    }
    else {
      dam = 25; 
    }
  }
  else {
    dam = atk->damage->roll();
  }
  
  if (dam > def->hp) {
     def->hp = 0;
  } else {
     def->hp = def->hp - dam;
  }
  status << def->name << " was attacked by " << atk->name << " for " << dam << " damage!";
  io_queue_message(status.str().c_str());

  status.str("");
  status << def->name << " has " << def->hp << " hp left!";
  io_queue_message(status.str().c_str());
  
  if (def->hp == 0) {
    character_die(def);
    if (def != d->pc) {
      d->num_monsters--;
    }
    return 0;
  }
  return 1;
}

int ranged_attack(dungeon_t *d) {
  int x, y, input, mon;
  std::stringstream stream;
  io_queue_message("Ranged attack selected");
  io_queue_message("Please choose monster to target (enter symbol) or hit escape to exit.");
  io_display(d);
  input = getch();
  if (input == 27) {
    io_display(d);
    return 1;
  }
  mon = input;
  //select monster
  for (y = d->pc->position[dim_y] - 3; y < d->pc->position[dim_y] + 4; y++) {
    for (x = d->pc->position[dim_x] - 3; x < d->pc->position[dim_x] + 4; x++) {
      if (d->charmap[y][x] && ((character *)d->charmap) != d->pc && can_see(d, character_get_pos(d->pc), character_get_pos(d->charmap[y][x]), 1)) {
	  if (d->charmap[y][x]->symbol == mon) {
	    stream << "Attack " << d->charmap[y][x]->name << " at " << ((y - character_get_y(d->pc)) <= 0 ? "North " : "South ") << (abs(y - character_get_y(d->pc)))
	       << ((character_get_x(d->pc) - x) <= 0 ? " East " : " West ") << (abs(character_get_x(d->pc) - x)) << " y/n ?";
	    io_queue_message(stream.str().c_str());
	    stream.str("");
	    io_display(d);
	    input = getch();
	    //attack monster
	    if (input == 121) {
	      if (((pc *)(d->pc))->equip_slot[2]) {
		stream << "Use " << ((pc *)(d->pc))->equip_slot[2]->get_name() << " (1) or cast poison ball spell (2) ?";
		io_queue_message(stream.str().c_str());
		io_display(d);
	        input = getch() - 48;
		if (input == 1) {
		  io_queue_message("Used ranged");
		  do_combat(d, d->pc, d->charmap[y][x], 1);
		  } else {
		  //splash damage
		  io_queue_message("Used poison ball spell");
		  if (d->charmap[y][x]) {do_combat(d, d->pc, d->charmap[y][x], 2);}
		  if (d->charmap[y - 1][x]) {do_combat(d, d->pc, d->charmap[y - 1][x], 2);}
		  if (d->charmap[y][x - 1]) {do_combat(d, d->pc, d->charmap[y][x - 1], 2);}
		  if (d->charmap[y - 1][x - 1]) {do_combat(d, d->pc, d->charmap[y - 1][x - 1], 2);}
		  if (d->charmap[y + 1][x - 1]) {do_combat(d, d->pc, d->charmap[y + 1][x - 1], 2);}
		  if (d->charmap[y - 1][x + 1]) {do_combat(d, d->pc, d->charmap[y - 1][x + 1], 2);}
		  if (d->charmap[y + 1][x]) {do_combat(d, d->pc, d->charmap[y + 1][x], 2);}
		  if (d->charmap[y][x + 1]) {do_combat(d, d->pc, d->charmap[y][x + 1], 2);}
		  if (d->charmap[y + 1][x + 1]) {do_combat(d, d->pc, d->charmap[y + 1][x + 1], 2);}
		  }
		} else {
		io_queue_message("No ranged weapons equiped...Used posion ball spell");
		if (d->charmap[y][x]) {do_combat(d, d->pc, d->charmap[y][x], 2);}
		if (d->charmap[y - 1][x]) {do_combat(d, d->pc, d->charmap[y - 1][x], 2);}
		if (d->charmap[y][x - 1]) {do_combat(d, d->pc, d->charmap[y][x - 1], 2);}
		if (d->charmap[y - 1][x - 1]) {do_combat(d, d->pc, d->charmap[y - 1][x - 1], 2);}
		if (d->charmap[y + 1][x - 1]) {do_combat(d, d->pc, d->charmap[y + 1][x - 1], 2);}
		if (d->charmap[y - 1][x + 1]) {do_combat(d, d->pc, d->charmap[y - 1][x + 1], 2);}
		if (d->charmap[y + 1][x]) {do_combat(d, d->pc, d->charmap[y + 1][x], 2);}
		if (d->charmap[y][x + 1]) {do_combat(d, d->pc, d->charmap[y][x + 1], 2);}
		if (d->charmap[y + 1][x + 1]) {do_combat(d, d->pc, d->charmap[y + 1][x + 1], 2);}
		io_queue_message("");
		}
	      io_display(d);
	      return 0;
	    }
	  }
      }
    }
  }
  io_queue_message("No monster attacked");
  io_display(d);
  return 1;
  }

void move_character(dungeon_t *d, character *c, pair_t next)
{
  if (charpair(next) &&
      ((next[dim_y] != character_get_y(c)) ||
       (next[dim_x] != character_get_x(c)))) {
    if  (do_combat(d, c, charpair(next),0)) {
	next[dim_x] = c->position[dim_x];
	next[dim_y] = c->position[dim_y];
    } else {
      d->charmap[character_get_y(c)][character_get_x(c)] = NULL;
      character_set_y(c, next[dim_y]);
      character_set_x(c, next[dim_x]);
      d->charmap[character_get_y(c)][character_get_x(c)] = c;
    }
  }
  else {
    d->charmap[character_get_y(c)][character_get_x(c)] = NULL;
    character_set_y(c, next[dim_y]);
    character_set_x(c, next[dim_x]);
    d->charmap[character_get_y(c)][character_get_x(c)] = c;
  }
  if (c == d->pc) {
    pc_reset_visibility(c);
    pc_observe_terrain(c, d);
  }
}

void do_moves(dungeon_t *d)
{
  pair_t next;
  character *c;
  int i;
  /* Remove the PC when it is PC turn.  Replace on next call.  This allows *
   * use to completely uninit the heap when generating a new level without *
   * worrying about deleting the PC.                                       */

  if (pc_is_alive(d)) {
    heap_insert(&d->next_turn, d->pc);
  }

  while (pc_is_alive(d) && ((c = ((character *)
                                  heap_remove_min(&d->next_turn))) != d->pc)) {
    if (!character_is_alive(c)) {
      if (d->charmap[character_get_y(c)][character_get_x(c)] == c) {
        d->charmap[character_get_y(c)][character_get_x(c)] = NULL;
      }
      if (c != d->pc) {
        character_delete(c);
      }
      continue;
    }

    character_next_turn(c);

    npc_next_pos(d, c, next);
    move_character(d, c, next);

    heap_insert(&d->next_turn, c);
  }

  io_display(d);

  if (pc_is_alive(d) && c == d->pc) {
    if (d->objmap[d->pc->position[dim_y]][d->pc->position[dim_x]]) {
      for (i = 0; i < 10 && ((pc *)(d->pc))->carry_slot[i]; i++) {}
    	    if (i != 10) {
	      ((pc *)(d->pc))->carry_slot[i] = d->objmap[d->pc->position[dim_y]][d->pc->position[dim_x]];
	      d->objmap[d->pc->position[dim_y]][d->pc->position[dim_x]] = NULL;
	  }
	}
    character_next_turn(c);
    io_handle_input(d);
  }
}

void dir_nearest_wall(dungeon_t *d, character *c, pair_t dir)
{
  dir[dim_x] = dir[dim_y] = 0;

  if (character_get_x(c) != 1 && character_get_x(c) != DUNGEON_X - 2) {
    dir[dim_x] = (character_get_x(c) > DUNGEON_X - character_get_x(c) ? 1 : -1);
  }
  if (character_get_y(c) != 1 && character_get_y(c) != DUNGEON_Y - 2) {
    dir[dim_y] = (character_get_y(c) > DUNGEON_Y - character_get_y(c) ? 1 : -1);
  }
}

uint32_t in_corner(dungeon_t *d, character *c)
{
  uint32_t num_immutable;

  num_immutable = 0;

  num_immutable += (mapxy(character_get_x(c) - 1,
                          character_get_y(c)    ) == ter_wall_immutable);
  num_immutable += (mapxy(character_get_x(c) + 1,
                          character_get_y(c)    ) == ter_wall_immutable);
  num_immutable += (mapxy(character_get_x(c)    ,
                          character_get_y(c) - 1) == ter_wall_immutable);
  num_immutable += (mapxy(character_get_x(c)    ,
                          character_get_y(c) + 1) == ter_wall_immutable);

  return num_immutable > 1;
}

static void new_dungeon_level(dungeon_t *d, uint32_t dir)
{
  /* Eventually up and down will be independantly meaningful. *
   * For now, simply generate a new dungeon.                  */

  switch (dir) {
  case '<':
  case '>':
    new_dungeon(d);
    break;
  default:
    break;
  }
}

uint32_t move_pc(dungeon_t *d, uint32_t dir)
{
  pair_t next;
  uint32_t was_stairs = 0;

  next[dim_y] = character_get_y(d->pc);
  next[dim_x] = character_get_x(d->pc);
  
  switch (dir) {
  case 1:
  case 2:
  case 3:
    next[dim_y]++;
    break;
  case 4:
  case 5:
  case 6:
    break;
  case 7:
  case 8:
  case 9:
    next[dim_y]--;
    break;
  }
  switch (dir) {
  case 1:
  case 4:
  case 7:
    next[dim_x]--;
    break;
  case 2:
  case 5:
  case 8:
    break;
  case 3:
  case 6:
  case 9:
    next[dim_x]++;
    break;
  case '<':
    if (mappair(character_get_pos(d->pc)) == ter_stairs_up) {
      was_stairs = 1;
      new_dungeon_level(d, '<');
    }
    break;
  case '>':
    if (mappair(character_get_pos(d->pc)) == ter_stairs_down) {
      was_stairs = 1;
      new_dungeon_level(d, '>');
    }
    break;
  }

  if (was_stairs) {
    return 0;
  }

  if ((dir != '>') && (dir != '<') && (mappair(next) >= ter_floor)) {
    move_character(d, d->pc, next);
    dijkstra(d);
    dijkstra_tunnel(d);

    return 0;
  }

  return 1;
}
