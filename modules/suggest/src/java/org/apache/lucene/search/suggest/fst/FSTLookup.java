begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Builder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FST
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FST
operator|.
name|Arc
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|NoOutputs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Outputs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|Lookup
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|TermFreqIterator
import|;
end_import
begin_comment
comment|/**  * Finite state automata based implementation of {@link Lookup} query   * suggestion/ autocomplete interface.  *   *<h2>Implementation details</h2>   *   *<p>The construction step in {@link #build(TermFreqIterator)} works as follows:  *<ul>  *<li>A set of input terms (String) and weights (float) is given.</li>  *<li>The range of weights is determined and then all weights are discretized into a fixed set   * of values ({@link #buckets}).  * Note that this means that minor changes in weights may be lost during automaton construction.   * In general, this is not a big problem because the "priorities" of completions can be split  * into a fixed set of classes (even as rough as: very frequent, frequent, baseline, marginal).  * If you need exact, fine-grained weights, use {@link TSTLookup} instead.<li>  *<li>All terms in the input are preprended with a synthetic pseudo-character being the weight  * of that term. For example a term<code>abc</code> with a discretized weight equal '1' would  * become<code>1abc</code>.</li>   *<li>The terms are sorted by their raw value of utf16 character values (including the synthetic  * term in front).</li>  *<li>A finite state automaton ({@link FST}) is constructed from the input. The root node has  * arcs labeled with all possible weights. We cache all these arcs, highest-weight first.</li>     *</ul>  *   *<p>At runtime, in {@link #lookup(String, boolean, int)}, the automaton is utilized as follows:  *<ul>  *<li>For each possible term weight encoded in the automaton (cached arcs from the root above),   * starting with the highest one, we descend along the path of the input key. If the key is not  * a prefix of a sequence in the automaton (path ends prematurely), we exit immediately.   * No completions.  *<li>Otherwise, we have found an internal automaton node that ends the key.<b>The entire  * subautomaton (all paths) starting from this node form the key's completions.</b> We start  * the traversal of this subautomaton. Every time we reach a final state (arc), we add a single  * suggestion to the list of results (the weight of this suggestion is constant and equal to the  * root path we started from). The tricky part is that because automaton edges are sorted and  * we scan depth-first, we can terminate the entire procedure as soon as we collect enough   * suggestions the user requested.  *<li>In case the number of suggestions collected in the step above is still insufficient,  * we proceed to the next (smaller) weight leaving the root node and repeat the same   * algorithm again.   *</li>  *</ul>  *    *<h2>Runtime behavior and performance characteristic</h2>  *   *<p>The algorithm described above is optimized for finding suggestions to short prefixes  * in a top-weights-first order. This is probably the most common use case: it allows   * presenting suggestions early and sorts them by the global frequency (and then alphabetically).  *   *<p>If there is an exact match in the automaton, it is returned first on the results  * list (even with by-weight sorting).  *   *<p>Note that the maximum lookup time for<b>any prefix</b>  * is the time of descending to the subtree, plus traversal of the subtree up to the number  * of requested suggestions (because they are already presorted by weight on the root level  * and alphabetically at any node level).  *   *<p>To order alphabetically only (no ordering by priorities), use identical term weights   * for all terms. Alphabetical suggestions are returned even if non-constant weights are  * used, but the algorithm for doing this is suboptimal.    *   *<p>"alphabetically" in any of the documentation above indicates utf16 codepoint order,   * nothing else.  */
end_comment
begin_class
DECL|class|FSTLookup
specifier|public
class|class
name|FSTLookup
extends|extends
name|Lookup
block|{
DECL|method|FSTLookup
specifier|public
name|FSTLookup
parameter_list|()
block|{
name|this
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|FSTLookup
specifier|public
name|FSTLookup
parameter_list|(
name|int
name|buckets
parameter_list|,
name|boolean
name|exactMatchFirst
parameter_list|)
block|{
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|exactMatchFirst
operator|=
name|exactMatchFirst
expr_stmt|;
block|}
comment|/** A structure for a single entry (for sorting/ preprocessing). */
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|term
name|char
index|[]
name|term
decl_stmt|;
DECL|field|weight
name|float
name|weight
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|char
index|[]
name|term
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|freq
expr_stmt|;
block|}
block|}
comment|/** Serialized automaton file name (storage). */
DECL|field|FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"fst.dat"
decl_stmt|;
comment|/** An empty result. */
DECL|field|EMPTY_RESULT
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|LookupResult
argument_list|>
name|EMPTY_RESULT
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|/**    * The number of separate buckets for weights (discretization). The more buckets,    * the more fine-grained term weights (priorities) can be assigned. The speed of lookup    * will not decrease for prefixes which have highly-weighted completions (because these    * are filled-in first), but will decrease significantly for low-weighted terms (but    * these should be infrequent, so it is all right).    *     *<p>The number of buckets must be within [1, 255] range.    */
DECL|field|buckets
specifier|private
specifier|final
name|int
name|buckets
decl_stmt|;
comment|/**    * If<code>true</code>, exact suggestions are returned first, even if they are prefixes    * of other strings in the automaton (possibly with larger weights).     */
DECL|field|exactMatchFirst
specifier|private
specifier|final
name|boolean
name|exactMatchFirst
decl_stmt|;
comment|/**    * Finite state automaton encoding all the lookup terms. See class    * notes for details.    */
DECL|field|automaton
specifier|private
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
decl_stmt|;
comment|/**    * An array of arcs leaving the root automaton state and encoding weights of all    * completions in their sub-trees.    */
DECL|field|rootArcs
specifier|private
name|Arc
argument_list|<
name|Object
argument_list|>
index|[]
name|rootArcs
decl_stmt|;
comment|/* */
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Buffer the input because we will need it twice: for calculating
comment|// weights distribution and for the actual automata building.
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|Entry
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|tfit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|term
init|=
name|tfit
operator|.
name|next
argument_list|()
decl_stmt|;
name|char
index|[]
name|termChars
init|=
operator|new
name|char
index|[
name|term
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
comment|// add padding for weight.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|term
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
name|termChars
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|term
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|termChars
argument_list|,
name|tfit
operator|.
name|freq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Distribute weights into at most N buckets. This is a form of discretization to
comment|// limit the number of possible weights so that they can be efficiently encoded in the
comment|// automaton.
comment|//
comment|// It is assumed the distribution of weights is _linear_ so proportional division
comment|// of [min, max] range will be enough here. Other approaches could be to sort
comment|// weights and divide into proportional ranges.
if|if
condition|(
name|entries
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|redistributeWeightsProportionalMinMax
argument_list|(
name|entries
argument_list|,
name|buckets
argument_list|)
expr_stmt|;
name|encodeWeightPrefix
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
comment|// Build the automaton (includes input sorting) and cache root arcs in order from the highest,
comment|// to the lowest weight.
name|this
operator|.
name|automaton
operator|=
name|buildAutomaton
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|cacheRootArcs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Cache the root node's output arcs starting with completions with the highest weights.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cacheRootArcs
specifier|private
name|void
name|cacheRootArcs
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|automaton
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|>
name|rootArcs
init|=
operator|new
name|ArrayList
argument_list|<
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
name|automaton
operator|.
name|getFirstArc
argument_list|(
operator|new
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|rootArcs
operator|.
name|add
argument_list|(
operator|new
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
break|break;
name|automaton
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|rootArcs
argument_list|)
expr_stmt|;
comment|// we want highest weights first.
name|this
operator|.
name|rootArcs
operator|=
name|rootArcs
operator|.
name|toArray
argument_list|(
operator|new
name|Arc
index|[
name|rootArcs
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Not implemented.    */
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// This implementation does not support ad-hoc additions (all input
comment|// must be sorted for the builder).
return|return
literal|false
return|;
block|}
comment|/**    * Get the (approximated) weight of a single key (if there is a perfect match    * for it in the automaton).     *     * @return Returns the approximated weight of the input key or<code>null</code>    * if not found.    */
annotation|@
name|Override
DECL|method|get
specifier|public
name|Float
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getExactMatchStartingFromRootArc
argument_list|(
literal|0
argument_list|,
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns the first exact match by traversing root arcs, starting from     * the arc<code>i</code>.    *     * @param i The first root arc index in {@link #rootArcs} to consider when    * matching.     */
DECL|method|getExactMatchStartingFromRootArc
specifier|private
name|Float
name|getExactMatchStartingFromRootArc
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|key
parameter_list|)
block|{
comment|// Get the UTF-8 bytes representation of the input key.
try|try
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|scratch
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|rootArcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|rootArc
init|=
name|rootArcs
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
name|scratch
operator|.
name|copyFrom
argument_list|(
name|rootArc
argument_list|)
decl_stmt|;
comment|// Descend into the automaton using the key as prefix.
if|if
condition|(
name|descendWithPrefix
argument_list|(
name|arc
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
comment|// Prefix-encoded weight.
return|return
name|rootArc
operator|.
name|label
operator|/
operator|(
name|float
operator|)
name|buckets
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Should never happen, but anyway.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Lookup autocomplete suggestions to<code>key</code>.    *      * @param key The prefix to which suggestions should be sought.     * @param onlyMorePopular Return most popular suggestions first. This is the default    * behavior for this implementation. Setting it to<code>false</code> has no effect (use    * constant term weights to sort alphabetically only).     * @param num At most this number of suggestions will be returned.    * @return Returns the suggestions, sorted by their approximated weight first (decreasing)    * and then alphabetically (utf16 codepoint order).    */
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|automaton
operator|==
literal|null
condition|)
block|{
comment|// Keep the result an ArrayList to keep calls monomorphic.
return|return
name|EMPTY_RESULT
return|;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|onlyMorePopular
operator|&&
name|rootArcs
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// We could emit a warning here (?). An optimal strategy for alphabetically sorted
comment|// suggestions would be to add them with a constant weight -- this saves unnecessary
comment|// traversals and sorting.
return|return
name|lookupSortedAlphabetically
argument_list|(
name|key
argument_list|,
name|num
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|lookupSortedByWeight
argument_list|(
name|key
argument_list|,
name|num
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Should never happen, but anyway.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Lookup suggestions sorted alphabetically<b>if weights are not constant</b>. This    * is a workaround: in general, use constant weights for alphabetically sorted result.    */
DECL|method|lookupSortedAlphabetically
specifier|private
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookupSortedAlphabetically
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Greedily get num results from each weight branch.
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
name|lookupSortedByWeight
argument_list|(
name|key
argument_list|,
name|num
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Sort and trim.
name|Collections
operator|.
name|sort
argument_list|(
name|res
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
block|{
comment|// not till java6 @Override
specifier|public
name|int
name|compare
parameter_list|(
name|LookupResult
name|o1
parameter_list|,
name|LookupResult
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|key
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|key
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>
name|num
condition|)
block|{
name|res
operator|=
name|res
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Lookup suggestions sorted by weight (descending order).    *     * @param greedy If<code>true</code>, the routine terminates immediately when<code>num</code>    * suggestions have been collected. If<code>false</code>, it will collect suggestions from    * all weight arcs (needed for {@link #lookupSortedAlphabetically}.    */
DECL|method|lookupSortedByWeight
specifier|private
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
name|lookupSortedByWeight
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|greedy
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|num
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|(
name|key
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchLength
init|=
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rootArcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|rootArc
init|=
name|rootArcs
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|rootArc
argument_list|)
decl_stmt|;
comment|// Descend into the automaton using the key as prefix.
if|if
condition|(
name|descendWithPrefix
argument_list|(
name|arc
argument_list|,
name|key
argument_list|)
condition|)
block|{
comment|// Prefix-encoded weight.
specifier|final
name|float
name|weight
init|=
name|rootArc
operator|.
name|label
operator|/
operator|(
name|float
operator|)
name|buckets
decl_stmt|;
comment|// A subgraph starting from the current node has the completions
comment|// of the key prefix. The arc we're at is the last key's byte,
comment|// so we will collect it too.
name|output
operator|.
name|setLength
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|collect
argument_list|(
name|res
argument_list|,
name|num
argument_list|,
name|weight
argument_list|,
name|output
argument_list|,
name|arc
argument_list|)
operator|&&
name|greedy
condition|)
block|{
comment|// We have enough suggestion to return immediately. Keep on looking for an
comment|// exact match, if requested.
if|if
condition|(
name|exactMatchFirst
condition|)
block|{
name|Float
name|exactMatchWeight
init|=
name|getExactMatchStartingFromRootArc
argument_list|(
name|i
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|exactMatchWeight
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|LookupResult
argument_list|(
name|key
argument_list|,
name|exactMatchWeight
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>
name|num
condition|)
block|{
name|res
operator|.
name|remove
argument_list|(
name|res
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Descend along the path starting at<code>arc</code> and going through    * bytes in<code>utf8</code> argument.    *      * @param arc The starting arc. This argument is modified in-place.    * @param term The term to descend with.    * @return If<code>true</code>,<code>arc</code> will be set to the arc matching    * last byte of<code>utf8</code>.<code>false</code> is returned if no such     * prefix<code>utf8</code> exists.    */
DECL|method|descendWithPrefix
specifier|private
name|boolean
name|descendWithPrefix
parameter_list|(
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
parameter_list|,
name|String
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|max
init|=
name|term
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|automaton
operator|.
name|findTargetArc
argument_list|(
name|term
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|&
literal|0xffff
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// No matching prefixes, return an empty result.
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Recursive collect lookup results from the automaton subgraph starting at<code>arc</code>.    *     * @param num Maximum number of results needed (early termination).    * @param weight Weight of all results found during this collection.    */
DECL|method|collect
specifier|private
name|boolean
name|collect
parameter_list|(
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
parameter_list|,
name|int
name|num
parameter_list|,
name|float
name|weight
parameter_list|,
name|StringBuilder
name|output
parameter_list|,
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|,
name|weight
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>=
name|num
condition|)
return|return
literal|true
return|;
block|}
else|else
block|{
name|int
name|save
init|=
name|output
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|collect
argument_list|(
name|res
argument_list|,
name|num
argument_list|,
name|weight
argument_list|,
name|output
argument_list|,
operator|new
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|output
operator|.
name|setLength
argument_list|(
name|save
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
break|break;
block|}
name|automaton
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Builds the final automaton from a list of entries.     */
DECL|method|buildAutomaton
specifier|private
name|FST
argument_list|<
name|Object
argument_list|>
name|buildAutomaton
parameter_list|(
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|entries
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// Sort by utf16 (raw char value)
specifier|final
name|Comparator
argument_list|<
name|Entry
argument_list|>
name|comp
init|=
operator|new
name|Comparator
argument_list|<
name|Entry
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Entry
name|o1
parameter_list|,
name|Entry
name|o2
parameter_list|)
block|{
name|char
index|[]
name|ch1
init|=
name|o1
operator|.
name|term
decl_stmt|;
name|char
index|[]
name|ch2
init|=
name|o2
operator|.
name|term
decl_stmt|;
name|int
name|len1
init|=
name|ch1
operator|.
name|length
decl_stmt|;
name|int
name|len2
init|=
name|ch2
operator|.
name|length
decl_stmt|;
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
name|len1
argument_list|,
name|len2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|int
name|v
init|=
name|ch1
index|[
name|i
index|]
operator|-
name|ch2
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|0
condition|)
return|return
name|v
return|;
block|}
return|return
name|len1
operator|-
name|len2
return|;
block|}
block|}
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
name|comp
argument_list|)
expr_stmt|;
comment|// Avoid duplicated identical entries, if possible. This is required because
comment|// it breaks automaton construction otherwise.
name|int
name|len
init|=
name|entries
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|entries
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|entries
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|entries
operator|.
name|set
argument_list|(
operator|++
name|j
argument_list|,
name|entries
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|entries
operator|=
name|entries
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|j
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Build the automaton.
specifier|final
name|Outputs
argument_list|<
name|Object
argument_list|>
name|outputs
init|=
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|empty
init|=
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Object
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<
name|Object
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|IntsRef
name|scratchIntsRef
init|=
operator|new
name|IntsRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
name|e
range|:
name|entries
control|)
block|{
specifier|final
name|int
name|termLength
init|=
name|scratchIntsRef
operator|.
name|length
operator|=
name|e
operator|.
name|term
operator|.
name|length
decl_stmt|;
name|scratchIntsRef
operator|.
name|grow
argument_list|(
name|termLength
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|ints
init|=
name|scratchIntsRef
operator|.
name|ints
decl_stmt|;
specifier|final
name|char
index|[]
name|chars
init|=
name|e
operator|.
name|term
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|termLength
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|ints
index|[
name|i
index|]
operator|=
name|chars
index|[
name|i
index|]
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|scratchIntsRef
argument_list|,
name|empty
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|finish
argument_list|()
return|;
block|}
comment|/**    * Prepends the entry's weight to each entry, encoded as a single byte, so that the    * root automaton node fans out to all possible priorities, starting with the arc that has    * the highest weights.         */
DECL|method|encodeWeightPrefix
specifier|private
name|void
name|encodeWeightPrefix
parameter_list|(
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|)
block|{
for|for
control|(
name|Entry
name|e
range|:
name|entries
control|)
block|{
name|int
name|weight
init|=
operator|(
name|int
operator|)
name|e
operator|.
name|weight
decl_stmt|;
assert|assert
operator|(
name|weight
operator|>=
literal|0
operator|&&
name|weight
operator|<=
name|buckets
operator|)
operator|:
literal|"Weight out of range: "
operator|+
name|weight
operator|+
literal|" ["
operator|+
name|buckets
operator|+
literal|"]"
assert|;
comment|// There should be a single empty char reserved in front for the weight.
name|e
operator|.
name|term
index|[
literal|0
index|]
operator|=
operator|(
name|char
operator|)
name|weight
expr_stmt|;
block|}
block|}
comment|/**    *  Split [min, max] range into buckets, reassigning weights. Entries' weights are    *  remapped to [0, buckets] range (so, buckets + 1 buckets, actually).    */
DECL|method|redistributeWeightsProportionalMinMax
specifier|private
name|void
name|redistributeWeightsProportionalMinMax
parameter_list|(
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|,
name|int
name|buckets
parameter_list|)
block|{
name|float
name|min
init|=
name|entries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|weight
decl_stmt|;
name|float
name|max
init|=
name|min
decl_stmt|;
for|for
control|(
name|Entry
name|e
range|:
name|entries
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|e
operator|.
name|weight
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|e
operator|.
name|weight
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
specifier|final
name|float
name|range
init|=
name|max
operator|-
name|min
decl_stmt|;
for|for
control|(
name|Entry
name|e
range|:
name|entries
control|)
block|{
name|e
operator|.
name|weight
operator|=
call|(
name|int
call|)
argument_list|(
name|buckets
operator|*
operator|(
operator|(
name|e
operator|.
name|weight
operator|-
name|min
operator|)
operator|/
name|range
operator|)
argument_list|)
expr_stmt|;
comment|// int cast equiv. to floor()
block|}
block|}
comment|/**    * Deserialization from disk.    */
annotation|@
name|Override
DECL|method|load
specifier|public
specifier|synchronized
name|boolean
name|load
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|data
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|data
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|InputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|automaton
operator|=
operator|new
name|FST
argument_list|<
name|Object
argument_list|>
argument_list|(
operator|new
name|InputStreamDataInput
argument_list|(
name|is
argument_list|)
argument_list|,
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
expr_stmt|;
name|cacheRootArcs
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Serialization to disk.    */
annotation|@
name|Override
DECL|method|store
specifier|public
specifier|synchronized
name|boolean
name|store
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|storeDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|storeDir
operator|.
name|isDirectory
argument_list|()
operator|||
operator|!
name|storeDir
operator|.
name|canWrite
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|automaton
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|FILENAME
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|automaton
operator|.
name|save
argument_list|(
operator|new
name|OutputStreamDataOutput
argument_list|(
name|os
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
