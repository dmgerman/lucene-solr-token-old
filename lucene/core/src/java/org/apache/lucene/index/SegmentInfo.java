begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
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
name|codecs
operator|.
name|Codec
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|TrackingDirectoryWrapper
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
name|StringHelper
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Information about a segment such as its name, directory, and files related  * to the segment.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SegmentInfo
specifier|public
specifier|final
class|class
name|SegmentInfo
block|{
comment|// TODO: remove these from this class, for now this is the representation
comment|/** Used by some member fields to mean not present (e.g.,    *  norms, deletions). */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|int
name|NO
init|=
operator|-
literal|1
decl_stmt|;
comment|// e.g. no norms; no deletes;
comment|/** Used by some member fields to mean present (e.g.,    *  norms, deletions). */
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|int
name|YES
init|=
literal|1
decl_stmt|;
comment|// e.g. have norms; have deletes;
comment|/** Unique segment name in the directory. */
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
comment|// number of docs in seg
comment|/** Where this segment resides. */
DECL|field|dir
specifier|public
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|isCompoundFile
specifier|private
name|boolean
name|isCompoundFile
decl_stmt|;
comment|/** Id that uniquely identifies this segment. */
DECL|field|id
specifier|private
specifier|final
name|byte
index|[]
name|id
decl_stmt|;
DECL|field|codec
specifier|private
name|Codec
name|codec
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
decl_stmt|;
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
comment|// Tracks the Lucene version this segment was created with, since 3.1. Null
comment|// indicates an older than 3.0 index, and it's used to detect a too old index.
comment|// The format expected is "x.y" - "2.x" for pre-3.0 indexes (or null), and
comment|// specific versions afterwards ("3.0.0", "3.1.0" etc.).
comment|// see o.a.l.util.Version.
DECL|field|version
specifier|private
name|Version
name|version
decl_stmt|;
DECL|method|setDiagnostics
name|void
name|setDiagnostics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
comment|/** Returns diagnostics saved into the segment when it was    *  written. */
DECL|method|getDiagnostics
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
comment|/**    * Construct a new complete SegmentInfo instance from input.    *<p>Note: this is public only to allow access from    * the codecs package.</p>    */
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Version
name|version
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|,
name|Codec
name|codec
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|,
name|byte
index|[]
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
assert|assert
operator|!
operator|(
name|dir
operator|instanceof
name|TrackingDirectoryWrapper
operator|)
assert|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|length
operator|!=
name|StringHelper
operator|.
name|ID_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid id: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|attributes
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Mark whether this segment is stored as a compound file.    *    * @param isCompoundFile true if this is a compound file;    * else, false    */
DECL|method|setUseCompoundFile
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|isCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
block|}
comment|/**    * Returns true if this segment is stored as a compound    * file; else, false.    */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
block|{
return|return
name|isCompoundFile
return|;
block|}
comment|/** Can only be called once. */
DECL|method|setCodec
specifier|public
name|void
name|setCodec
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|codec
operator|==
literal|null
assert|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec must be non-null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
block|}
comment|/** Return {@link Codec} that wrote this segment. */
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|/** Returns number of documents in this segment (deletions    *  are not taken into account). */
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|docCount
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"docCount isn't set yet"
argument_list|)
throw|;
block|}
return|return
name|docCount
return|;
block|}
comment|// NOTE: leave package private
DECL|method|setDocCount
name|void
name|setDocCount
parameter_list|(
name|int
name|docCount
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|docCount
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"docCount was already set: this.docCount="
operator|+
name|this
operator|.
name|docCount
operator|+
literal|" vs docCount="
operator|+
name|docCount
argument_list|)
throw|;
block|}
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
block|}
comment|/** Return all files referenced by this SegmentInfo. */
DECL|method|files
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|()
block|{
if|if
condition|(
name|setFiles
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"files were not computed yet"
argument_list|)
throw|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|setFiles
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/** Used for debugging.  Format may suddenly change.    *    *<p>Current format looks like    *<code>_a(3.1):c45/4</code>, which means the segment's    *  name is<code>_a</code>; it was created with Lucene 3.1 (or    *  '?' if it's unknown); it's using compound file    *  format (would be<code>C</code> if not compound); it    *  has 45 documents; it has 4 deletions (this part is    *  left off when there are no deletions).</p>    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|delCount
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|version
operator|==
literal|null
condition|?
literal|"?"
else|:
name|version
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|char
name|cfs
init|=
name|getUseCompoundFile
argument_list|()
condition|?
literal|'c'
else|:
literal|'C'
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|delCount
operator|!=
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|delCount
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we could append toString of attributes() here?
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** We consider another SegmentInfo instance equal if it    *  has the same dir and same name. */
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
operator|instanceof
name|SegmentInfo
condition|)
block|{
specifier|final
name|SegmentInfo
name|other
init|=
operator|(
name|SegmentInfo
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|dir
operator|==
name|dir
operator|&&
name|other
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
name|dir
operator|.
name|hashCode
argument_list|()
operator|+
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the version of the code which wrote the segment.    */
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/** Return the id that uniquely identifies this segment. */
DECL|method|getId
specifier|public
name|byte
index|[]
name|getId
parameter_list|()
block|{
return|return
name|id
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|field|setFiles
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|setFiles
decl_stmt|;
comment|/** Sets the files written for this segment. */
DECL|method|setFiles
specifier|public
name|void
name|setFiles
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|setFiles
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|addFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
comment|/** Add these files to the set of files written for this    *  segment. */
DECL|method|addFiles
specifier|public
name|void
name|addFiles
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|checkFileNames
argument_list|(
name|files
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|f
range|:
name|files
control|)
block|{
name|setFiles
operator|.
name|add
argument_list|(
name|namedForThisSegment
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Add this file to the set of files written for this    *  segment. */
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|checkFileNames
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|setFiles
operator|.
name|add
argument_list|(
name|namedForThisSegment
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFileNames
specifier|private
name|void
name|checkFileNames
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|IndexFileNames
operator|.
name|CODEC_FILE_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|m
operator|.
name|reset
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid codec filename '"
operator|+
name|file
operator|+
literal|"', must match: "
operator|+
name|IndexFileNames
operator|.
name|CODEC_FILE_PATTERN
operator|.
name|pattern
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**     * strips any segment name from the file, naming it with this segment    * this is because "segment names" can change, e.g. by addIndexes(Dir)    */
DECL|method|namedForThisSegment
name|String
name|namedForThisSegment
parameter_list|(
name|String
name|file
parameter_list|)
block|{
return|return
name|name
operator|+
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**    * Get a codec attribute value, or null if it does not exist    */
DECL|method|getAttribute
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Puts a codec attribute value.    *<p>    * This is a key-value mapping for the field that the codec can use to store    * additional metadata, and will be available to the codec when reading the    * segment via {@link #getAttribute(String)}    *<p>    * If a value already exists for the field, it will be replaced with the new    * value.    */
DECL|method|putAttribute
specifier|public
name|String
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Returns the internal codec attributes map.    * @return internal codec attributes map.    */
DECL|method|getAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
block|}
end_class
end_unit
