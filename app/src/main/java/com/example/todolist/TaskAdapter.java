package com.example.todolist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.ItemTaskBinding;

import java.util.Date;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private final OnTaskClickListener clickListener;
    private final OnTaskCheckListener checkListener;
    private final OnTaskMenuClickListener menuClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskCheckListener {
        void onTaskCheck(Task task);
    }

    public interface OnTaskMenuClickListener {
        void onTaskMenuClick(Task task, View view);
    }

    public TaskAdapter(OnTaskClickListener clickListener, OnTaskCheckListener checkListener) {
        this(clickListener, checkListener, null);
    }

    public TaskAdapter(OnTaskClickListener clickListener, OnTaskCheckListener checkListener, OnTaskMenuClickListener menuClickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.checkListener = checkListener;
        this.menuClickListener = menuClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task, clickListener, checkListener, menuClickListener);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Task task, OnTaskClickListener clickListener, OnTaskCheckListener checkListener, OnTaskMenuClickListener menuClickListener) {
            // Titre et description
            binding.tvTaskTitle.setText(task.getTitle());
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                binding.tvDescription.setText(task.getDescription());
                binding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvDescription.setVisibility(View.GONE);
            }

            // Style du texte si la tâche est complétée
            if (task.isCompleted()) {
                binding.tvTaskTitle.setPaintFlags(binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTaskTitle.setAlpha(0.6f);
                binding.tvDescription.setAlpha(0.6f);
            } else {
                binding.tvTaskTitle.setPaintFlags(binding.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.tvTaskTitle.setAlpha(1.0f);
                binding.tvDescription.setAlpha(1.0f);
            }

            // Priorité
            ViewExtensions.setPriorityIndicatorColor(binding.priorityIndicator, task.getPriority());

            // Date d'échéance
            Date dueDate = DateUtils.parseDateTime(task.getDueDate());
            if (dueDate != null) {
                binding.tvDueDate.setText(DateUtils.formatDisplayDate(dueDate));

                // Changer la couleur de la date si la tâche est en retard
                if (DateUtils.isPastDate(dueDate) && !task.isCompleted()) {
                    binding.tvDueDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.error));
                } else {
                    binding.tvDueDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_secondary));
                }
            }

            // État de la case à cocher
            binding.checkboxTask.setChecked(task.isCompleted());

            // Actions
            binding.getRoot().setOnClickListener(v -> clickListener.onTaskClick(task));
            binding.checkboxTask.setOnCheckedChangeListener(null); // Éviter les appels récursifs
            binding.checkboxTask.setChecked(task.isCompleted());
            binding.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkListener.onTaskCheck(task);
            });

            // Menu d'options (si disponible)
            if (menuClickListener != null) {
                binding.ivMore.setVisibility(View.VISIBLE);
                binding.ivMore.setOnClickListener(v -> menuClickListener.onTaskMenuClick(task, v));
            } else {
                binding.ivMore.setVisibility(View.GONE);
            }

            // Catégorie
            // TODO: Implémenter l'affichage du nom de la catégorie
            // Pour le moment, cacher le chip de catégorie
            binding.tvCategory.setVisibility(View.GONE);
        }
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getDueDate().equals(newItem.getDueDate())
                    && oldItem.getPriority() == newItem.getPriority()
                    && oldItem.isCompleted() == newItem.isCompleted()
                    && oldItem.getCategoryId() == newItem.getCategoryId();
        }
    };
}