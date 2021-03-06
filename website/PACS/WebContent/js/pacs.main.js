/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
# Copyright (C) 2015 CSISS, GMU (http://csiss.gmu.edu),
# Ziheng Sun (szhwhu@gmail.com)
#
# This source is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.
#
# This code is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# A copy of the GNU General Public License is available on the World Wide Web
# at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by writing
# to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
# MA 02111-1307, USA.
#
#******************************************************************************
*/
/**
 * PACS
 * Virtual Data Product Query Interface
 * Main Function
 * Entrance Function
 * First invoked Function
 * Require pacs.js pacs.util.js pacs.button.js pacs.map.js pacs.list.js
 * @author Ziheng Sun
 * @date 2015.8.4
 */
pacs.main = {
		/**
		 * Load PACS
		 */
		load: function(){
			pacs.util.init();
			if (typeof pacs.map != 'undefined')
				pacs.map.init();
			if (typeof pacs.button != 'undefined')
				pacs.button.init();
			if (typeof pacs.list != 'undefined')
				pacs.list.init();
		}
};

pacs.main.load();
