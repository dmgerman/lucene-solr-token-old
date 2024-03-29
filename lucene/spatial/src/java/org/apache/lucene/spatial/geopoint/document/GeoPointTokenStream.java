begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.geopoint.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|Attribute
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
name|AttributeFactory
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
name|AttributeImpl
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
name|AttributeReflector
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
name|BytesRef
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
name|BytesRefBuilder
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|geoCodedToPrefixCoded
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|PRECISION_STEP
import|;
end_import
begin_comment
comment|/**  *<b>Expert:</b> This class provides a {@link TokenStream} used by {@link GeoPointField}  * for encoding {@link GeoPointField.TermEncoding#PREFIX} only GeoPointTerms.  *  *<p><i>NOTE: This is used as the default encoding unless  * {@code GeoPointField.setNumericType(FieldType.LegacyNumericType.LONG)} is set</i></p>  *  * This class is similar to {@link org.apache.lucene.analysis.LegacyNumericTokenStream} but encodes terms up to a  * a maximum of {@link #MAX_SHIFT} using a fixed precision step defined by  * {@link GeoPointField#PRECISION_STEP}. This yields a total of 4 terms per GeoPoint  * each consisting of 5 bytes (4 prefix bytes + 1 precision byte).  *  *<p>For best performance use the provided {@link GeoPointField#PREFIX_TYPE_NOT_STORED} or  * {@link GeoPointField#PREFIX_TYPE_STORED}</p>  *  *<p>If prefix terms are used then the default GeoPoint query constructors may be used, but if  * {@link org.apache.lucene.analysis.LegacyNumericTokenStream} is used, then be sure to pass  * {@link GeoPointField.TermEncoding#NUMERIC} to all GeoPointQuery constructors</p>  *  * Here's an example usage:  *  *<pre class="prettyprint">  *   // using prefix terms  *   GeoPointField geoPointField = new GeoPointField(fieldName1, lat, lon, GeoPointField.PREFIX_TYPE_NOT_STORED);  *   document.add(geoPointField);  *  *   // query by bounding box (default uses TermEncoding.PREFIX)  *   Query q = new GeoPointInBBoxQuery(fieldName1, minLat, maxLat, minLon, maxLon);  *  *   // using numeric terms  *   geoPointField = new GeoPointField(fieldName2, lat, lon, GeoPointField.NUMERIC_TYPE_NOT_STORED);  *   document.add(geoPointField);  *  *   // query by distance (requires TermEncoding.NUMERIC)  *   q = new GeoPointDistanceQuery(fieldName2, TermEncoding.NUMERIC, centerLat, centerLon, radiusMeters);  *</pre>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointTokenStream
specifier|final
class|class
name|GeoPointTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|MAX_SHIFT
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SHIFT
init|=
name|PRECISION_STEP
operator|*
literal|4
decl_stmt|;
DECL|field|geoPointTermAtt
specifier|private
specifier|final
name|GeoPointTermAttribute
name|geoPointTermAtt
init|=
name|addAttribute
argument_list|(
name|GeoPointTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|isInit
specifier|private
name|boolean
name|isInit
init|=
literal|false
decl_stmt|;
comment|/**    * Expert: Creates a token stream for geo point fields with the specified    *<code>precisionStep</code> using the given    * {@link org.apache.lucene.util.AttributeFactory}.    * The stream is not yet initialized,    * before using set a value using the various setGeoCode method.    */
DECL|method|GeoPointTokenStream
specifier|public
name|GeoPointTokenStream
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|GeoPointAttributeFactory
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|PRECISION_STEP
operator|>
literal|0
assert|;
block|}
DECL|method|setGeoCode
specifier|public
name|GeoPointTokenStream
name|setGeoCode
parameter_list|(
specifier|final
name|long
name|geoCode
parameter_list|)
block|{
name|geoPointTermAtt
operator|.
name|init
argument_list|(
name|geoCode
argument_list|,
name|MAX_SHIFT
operator|-
name|PRECISION_STEP
argument_list|)
expr_stmt|;
name|isInit
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|isInit
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setGeoCode() before usage"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|isInit
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setGeoCode() before usage"
argument_list|)
throw|;
block|}
comment|// this will only clear all other attributes in this TokenStream
name|clearAttributes
argument_list|()
expr_stmt|;
specifier|final
name|int
name|shift
init|=
name|geoPointTermAtt
operator|.
name|incShift
argument_list|()
decl_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
operator|(
name|shift
operator|==
name|MAX_SHIFT
operator|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
return|return
operator|(
name|shift
operator|<
literal|63
operator|)
return|;
block|}
comment|/**    * Tracks shift values during encoding    */
DECL|interface|GeoPointTermAttribute
specifier|public
interface|interface
name|GeoPointTermAttribute
extends|extends
name|Attribute
block|{
comment|/** Returns current shift value, undefined before first token */
DECL|method|getShift
name|int
name|getShift
parameter_list|()
function_decl|;
comment|/**<em>Don't call this method!</em>      * @lucene.internal */
DECL|method|init
name|void
name|init
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|shift
parameter_list|)
function_decl|;
comment|/**<em>Don't call this method!</em>      * @lucene.internal */
DECL|method|incShift
name|int
name|incShift
parameter_list|()
function_decl|;
block|}
comment|// just a wrapper to prevent adding CTA
DECL|class|GeoPointAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|GeoPointAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|GeoPointAttributeFactory
name|GeoPointAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"GeoPointTokenStream does not support CharTermAttribute."
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
DECL|class|GeoPointTermAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|GeoPointTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|GeoPointTermAttribute
implements|,
name|TermToBytesRefAttribute
block|{
DECL|field|value
specifier|private
name|long
name|value
init|=
literal|0L
decl_stmt|;
DECL|field|shift
specifier|private
name|int
name|shift
init|=
literal|0
decl_stmt|;
DECL|field|bytes
specifier|private
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|GeoPointTermAttributeImpl
specifier|public
name|GeoPointTermAttributeImpl
parameter_list|()
block|{
name|this
operator|.
name|shift
operator|=
name|MAX_SHIFT
operator|-
name|PRECISION_STEP
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
name|geoCodedToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|shift
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getShift
specifier|public
name|int
name|getShift
parameter_list|()
block|{
return|return
name|shift
return|;
block|}
annotation|@
name|Override
DECL|method|incShift
specifier|public
name|int
name|incShift
parameter_list|()
block|{
return|return
operator|(
name|shift
operator|+=
name|PRECISION_STEP
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// this attribute has no contents to clear!
comment|// we keep it untouched as it's fully controlled by outer class.
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|reflector
operator|.
name|reflect
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|,
literal|"bytes"
argument_list|,
name|getBytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|GeoPointTermAttribute
operator|.
name|class
argument_list|,
literal|"shift"
argument_list|,
name|shift
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|GeoPointTermAttribute
name|a
init|=
operator|(
name|GeoPointTermAttribute
operator|)
name|target
decl_stmt|;
name|a
operator|.
name|init
argument_list|(
name|value
argument_list|,
name|shift
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|GeoPointTermAttributeImpl
name|clone
parameter_list|()
block|{
name|GeoPointTermAttributeImpl
name|t
init|=
operator|(
name|GeoPointTermAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Do a deep clone
name|t
operator|.
name|bytes
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
name|t
operator|.
name|bytes
operator|.
name|copyBytes
argument_list|(
name|getBytesRef
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|shift
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|GeoPointTermAttributeImpl
name|other
init|=
operator|(
name|GeoPointTermAttributeImpl
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|shift
operator|!=
name|other
operator|.
name|shift
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|value
operator|!=
name|other
operator|.
name|value
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
comment|/** override toString because it can throw cryptic "illegal shift value": */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(precisionStep="
operator|+
name|PRECISION_STEP
operator|+
literal|" shift="
operator|+
name|geoPointTermAtt
operator|.
name|getShift
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
