begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexOptions
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
name|NumericRangeQuery
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|NumericUtils
import|;
end_import
begin_comment
comment|/**  *<p>  * Field that indexes<code>double</code> values  * for efficient range filtering and sorting. Here's an example usage:  *   *<pre class="prettyprint">  * document.add(new DoubleField(name, 6.0, Field.Store.NO));  *</pre>  *   * For optimal performance, re-use the<code>DoubleField</code> and  * {@link Document} instance for more than one document:  *   *<pre class="prettyprint">  *  DoubleField field = new DoubleField(name, 0.0, Field.Store.NO);  *  Document document = new Document();  *  document.add(field);  *   *  for(all documents) {  *    ...  *    field.setDoubleValue(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *  * See also {@link IntField}, {@link LongField}, {@link  * FloatField}.  *  *<p>To perform range querying or filtering against a  *<code>DoubleField</code>, use {@link NumericRangeQuery}.  * To sort according to a  *<code>DoubleField</code>, use the normal numeric sort types, eg  * {@link org.apache.lucene.search.SortField.Type#DOUBLE}.<code>DoubleField</code>   * values can also be loaded directly from {@link org.apache.lucene.index.LeafReader#getNumericDocValues}.</p>  *  *<p>You may add the same field name as an<code>DoubleField</code> to  * the same document more than once.  Range querying and  * filtering will be the logical OR of all values; so a range query  * will hit all documents that have at least one value in  * the range. However sort behavior is not defined.  If you need to sort,  * you should separately index a single-valued<code>DoubleField</code>.</p>  *  *<p>A<code>DoubleField</code> will consume somewhat more disk space  * in the index than an ordinary single-valued field.  * However, for a typical index that includes substantial  * textual content per document, this increase will likely  * be in the noise.</p>  *  *<p>Within Lucene, each numeric value is indexed as a  *<em>trie</em> structure, where each term is logically  * assigned to larger and larger pre-defined brackets (which  * are simply lower-precision representations of the value).  * The step size between each successive bracket is called the  *<code>precisionStep</code>, measured in bits.  Smaller  *<code>precisionStep</code> values result in larger number  * of brackets, which consumes more disk space in the index  * but may result in faster range search performance.  The  * default value, 4, was selected for a reasonable tradeoff  * of disk space consumption versus performance.  You can  * create a custom {@link FieldType} and invoke the {@link  * FieldType#setNumericPrecisionStep} method if you'd  * like to change the value.  Note that you must also  * specify a congruent value when creating {@link  * NumericRangeQuery}.  * For low cardinality fields larger precision steps are good.  * If the cardinality is&lt; 100, it is fair  * to use {@link Integer#MAX_VALUE}, which produces one  * term per value.  *  *<p>For more information on the internals of numeric trie  * indexing, including the<a  * href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>  * configuration, see {@link NumericRangeQuery}. The format of  * indexed values is described in {@link NumericUtils}.  *  *<p>If you only need to sort by numeric value, and never  * run range querying/filtering, you can index using a  *<code>precisionStep</code> of {@link Integer#MAX_VALUE}.  * This will minimize disk space consumed.</p>  *  *<p>More advanced users can instead use {@link  * NumericTokenStream} directly, when indexing numbers. This  * class is a wrapper around this token stream type for  * easier, more intuitive usage.</p>  *  * @since 2.9  */
end_comment
begin_class
DECL|class|DoubleField
specifier|public
specifier|final
class|class
name|DoubleField
extends|extends
name|Field
block|{
comment|/**     * Type for a DoubleField that is not stored:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|NumericType
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Type for a stored DoubleField:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|NumericType
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a stored or un-stored DoubleField with the provided value    *  and default<code>precisionStep</code> {@link    *  NumericUtils#PRECISION_STEP_DEFAULT} (16).     *  @param name field name    *  @param value 64-bit double value    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name is null.     */
DECL|method|DoubleField
specifier|public
name|DoubleField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|stored
operator|==
name|Store
operator|.
name|YES
condition|?
name|TYPE_STORED
else|:
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: allows you to customize the {@link    *  FieldType}.     *  @param name field name    *  @param value 64-bit double value    *  @param type customized field type: must have {@link FieldType#numericType()}    *         of {@link FieldType.NumericType#DOUBLE}.    *  @throws IllegalArgumentException if the field name or type is null, or    *          if the field type does not have a DOUBLE numericType()    */
DECL|method|DoubleField
specifier|public
name|DoubleField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
name|FieldType
operator|.
name|NumericType
operator|.
name|DOUBLE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.numericType() must be DOUBLE but got "
operator|+
name|type
operator|.
name|numericType
argument_list|()
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
