package com.example.jobify


import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jobify.R
import com.example.jobify.model.ChatHelper
import com.example.jobify.model.ChatMessage

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageContainer: LinearLayout = view.findViewById(R.id.messageContainer)
        val messageCard: CardView = view.findViewById(R.id.messageCard)
        val messageText: TextView = view.findViewById(R.id.messageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val context = holder.itemView.context

        // Format le texte avec markdown basique
        val formattedText = ChatHelper.formatMarkdown(message.text)
        holder.messageText.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(formattedText)
        }

        if (message.isUser) {
            // Message utilisateur - aligné à droite
            holder.messageContainer.gravity = Gravity.END
            holder.messageCard.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.user_message_bg)
            )
            holder.messageText.setTextColor(
                ContextCompat.getColor(context, android.R.color.white)
            )

            val params = holder.messageCard.layoutParams as LinearLayout.LayoutParams
            params.marginStart = 80
            params.marginEnd = 16
            holder.messageCard.layoutParams = params
        } else {
            // Message bot - aligné à gauche
            holder.messageContainer.gravity = Gravity.START
            holder.messageCard.setCardBackgroundColor(
                ContextCompat.getColor(context, android.R.color.white)
            )
            holder.messageText.setTextColor(
                ContextCompat.getColor(context, R.color.bot_message_text)
            )

            val params = holder.messageCard.layoutParams as LinearLayout.LayoutParams
            params.marginStart = 16
            params.marginEnd = 80
            holder.messageCard.layoutParams = params
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clear() {
        messages.clear()
        notifyDataSetChanged()
    }
}