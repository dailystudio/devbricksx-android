package com.dailystudio.devbricksx.fragment

import androidx.fragment.app.Fragment

/**
 * Abstract Fragment representing a single page in a [AbsViewPagerFragment].
 *
 * It holds an item representing the page's data.
 *
 * @param PageItem The type of the item for the page.
 * @property item The item data for this page.
 */
abstract class AbsPageFragment<PageItem>(protected val item: PageItem): Fragment()